import http from 'k6/http';
import exec from 'k6/execution';
import { check, group, sleep } from 'k6';

const TEST_CONFIG = {
  baseUrl: 'http://localhost:8080',
  maxVus: 500,
  email: 'fady@gmail.com',
  password: 'test1234',
  resultsFile: 'StressTesting.summary.json',
};

const BASE_URL = TEST_CONFIG.baseUrl.replace(/\/$/, '');
const MAX_VUS = TEST_CONFIG.maxVus;

export const options = {
  scenarios: {
    ps_tracker_journey: {
      executor: 'ramping-vus',
      startVUs: 0,
      gracefulRampDown: '30s',
      stages: [
        { duration: '30s', target: Math.max(1, Math.ceil(MAX_VUS * 0.5)) },
        { duration: '1m', target: Math.max(1, Math.ceil(MAX_VUS * 0.75)) },
        { duration: '1m', target: Math.max(1, Math.ceil(MAX_VUS * 0.75)) },
        { duration: '3m', target: MAX_VUS },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    checks: ['rate>0.99'],
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
  },
};

function formatNumber(value) {
  if (!Number.isFinite(value)) {
    return 'n/a';
  }

  return Number.isInteger(value) ? String(value) : value.toFixed(2);
}

function getMetricValue(metrics, metricName, valueName) {
  const metric = metrics?.[metricName];
  const value = metric?.values?.[valueName];

  return Number.isFinite(value) ? value : undefined;
}

export function handleSummary(data) {
  const checksRate = getMetricValue(data.metrics, 'checks', 'rate');
  const httpFailedRate = getMetricValue(data.metrics, 'http_req_failed', 'rate');
  const httpP95 = getMetricValue(data.metrics, 'http_req_duration', 'p(95)');
  const httpP99 = getMetricValue(data.metrics, 'http_req_duration', 'p(99)');

  const summaryText = [
    'PsTracker stress test summary',
    `Base URL: ${BASE_URL}`,
    `Max VUs: ${MAX_VUS}`,
    `Checks rate: ${formatNumber((checksRate ?? 0) * 100)}%`,
    `HTTP failed rate: ${formatNumber((httpFailedRate ?? 0) * 100)}%`,
    `HTTP duration p95: ${formatNumber(httpP95)}ms`,
    `HTTP duration p99: ${formatNumber(httpP99)}ms`,
    '',
    'Project limits encoded in this script:',
    '- checks rate must stay above 99%',
    '- failed HTTP requests must stay below 1%',
    '- p95 HTTP duration must stay below 500ms',
    '- p99 HTTP duration must stay below 1000ms',
    '',
  ].join('\n');

  return {
    stdout: `${summaryText}\n`,
    [TEST_CONFIG.resultsFile]: JSON.stringify(data, null, 2),
  };
}

function isLocalTarget(url) {
  return /^https?:\/\/(localhost|127\.0\.0\.1)(:\d+)?(?:\/|$)/i.test(url);
}

export function setup() {
  if (!isLocalTarget(BASE_URL)) {
    exec.test.abort(
      `Refusing to load-test remote target ${BASE_URL}. Update TEST_CONFIG.baseUrl to a local target before running this script.`,
    );
  }

  const loginResponse = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({ email: TEST_CONFIG.email, password: TEST_CONFIG.password }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { endpoint: 'login' },
      timeout: '10s',
    },
  );

  const validLogin = check(loginResponse, {
    'login returned 200': (response) => response.status === 200,
    'login returned an access token': (response) => Boolean(response.json('token')),
  });

  if (!validLogin) {
    exec.test.abort(`Login failed with HTTP ${loginResponse.status}`);
  }

  return { token: loginResponse.json('token') };
}

const journeys = [
  { name: 'current user', path: '/api/users/me' },
  { name: 'my submissions', path: '/api/submissions/me' },
  { name: 'my reports', path: '/api/reports/me' },
  { name: 'my contests', path: '/api/contests/me' },
  { name: 'my notifications', path: '/api/notifications/me' },
];

export default function (data) {
  const journey = journeys[Math.floor(Math.random() * journeys.length)];

  group(journey.name, () => {
    const response = http.get(`${BASE_URL}${journey.path}`, {
      headers: {
        Accept: 'application/json',
        Authorization: `Bearer ${data.token}`,
      },
      tags: { endpoint: journey.name },
      timeout: '10s',
    });

    check(response, {
      [`${journey.name} returned 200`]: (result) => result.status === 200,
      [`${journey.name} returned JSON`]: (result) =>
        (result.headers['Content-Type'] || '').includes('application/json'),
    });
  });

  // Model a user reading the response before starting the next action.
  sleep(1 + Math.random() * 2);
}
