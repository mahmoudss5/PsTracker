import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AuthPage } from './pages/AuthPage';
import { ErrorPage } from './pages/ErrorPage';
import { RootLayout } from './pages/RootLayout';
import { TraineeDashboard } from './pages/TraineeDashboard';
import { CoachDashboard } from './pages/CoachDashboard';
import { InvalidUrl } from './components/shared/InvalidUrl';
import { TeamPage } from './pages/TeamPage';
import { SubmissionsPage } from './pages/SubmissionsPage';
import { useCurrentUser } from './hooks/useCurrentUser';

function DashboardIndexRedirect() {
  const { user, isLoading } = useCurrentUser();
  
  if (isLoading) return null;
  
  if (user?.role?.toLowerCase() === 'coach') {
    return <Navigate to="coach" replace />;
  }
  return <Navigate to="trainee" replace />;
}

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
        element: <DashboardIndexRedirect />,
      },
      {
        path: 'trainee',
        element: <TraineeDashboard />,
      },
      {
        path: 'coach',
        element: <CoachDashboard />,
      },
      {
        path: 'team',
        element: <TeamPage />,
      },
      {
        path: 'team/:teamId',
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
