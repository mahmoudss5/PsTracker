import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AuthPage } from './pages/AuthPage';
import { ErrorPage } from './pages/ErrorPage';
import { RootLayout } from './pages/RootLayout';
import { TraineeDashboard } from './pages/TraineeDashboard';

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
    ],
  },
  {
    path: '*',
    element: <Navigate to="/dashboard" replace />,
  },
]);
