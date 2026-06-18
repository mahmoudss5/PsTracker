import { AlertTriangle, ArrowLeft } from 'lucide-react';
import { isRouteErrorResponse, Link, useRouteError } from 'react-router-dom';

function getErrorMessage(error: unknown) {
  if (isRouteErrorResponse(error)) {
    return error.statusText || error.data?.message || 'The requested page could not be loaded.';
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'An unexpected error occurred.';
}

export function ErrorPage() {
  const error = useRouteError();

  return (
    <main className="grid min-h-screen place-items-center bg-dashboard px-4 text-dashboard-text">
      <section className="glass-panel w-full max-w-lg p-8 text-center">
        <AlertTriangle className="mx-auto text-dashboard-primary" size={40} />
        <h1 className="mt-5 text-2xl font-semibold">Something went wrong</h1>
        <p className="mt-3 text-dashboard-muted">{getErrorMessage(error)}</p>
        <Link to="/" className="primary-action mt-7">
          <ArrowLeft size={16} />
          Back to login
        </Link>
      </section>
    </main>
  );
}
