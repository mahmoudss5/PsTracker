import { Navigate } from 'react-router-dom';
import { useCurrentUser } from '../hooks/useCurrentUser';

export function DashboardIndexRedirect() {
  const { user, isLoading } = useCurrentUser();

  if (isLoading) return null;

  if (user?.role?.toLowerCase() === 'coach') {
    return <Navigate to="coach" replace />;
  }
  return <Navigate to="trainee" replace />;
}
