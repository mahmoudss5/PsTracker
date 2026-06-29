import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AuthPage } from './pages/AuthPage';
import { ErrorPage } from './pages/ErrorPage';
import { RootLayout } from './pages/RootLayout';
import { TraineeDashboard } from './pages/TraineeDashboard';
import { InvalidUrl } from './components/shared/InvalidUrl';
import { TeamPage } from './pages/TeamPage';
import { SubmissionsPage } from './pages/SubmissionsPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AuthPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: '/dashboard',
    element: <RootLayout />,
    errorElement: <ErrorPage />,
    children: [
      {
        index: true,
        element: <Navigate to="trainee" replace />,
      },
      {
        path: 'trainee',
        element: <TraineeDashboard />,
      },
      {
        path: 'team',
        element: <TeamPage />,
      },
      {
        path: 'submissions',
        element: <SubmissionsPage />,
      },
      {
        path: 'submissions/:handle',
        element: <SubmissionsPage />,
      },
    ],
  },
  {
    path: '*',
    element: <InvalidUrl />,
  },
]);

