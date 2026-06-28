import { Link } from "react-router-dom";
import { AlertCircle, Home } from "lucide-react";

export function InvalidUrl() {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-dashboard px-4 text-dashboard-text">
      {/* Ambient background blur blobs */}
      <div className="dashboard-ambient" />
      <div className="absolute top-1/4 left-1/4 -z-10 h-72 w-72 rounded-full bg-dashboard-primary/10 blur-[100px]" />
      <div className="absolute bottom-1/4 right-1/4 -z-10 h-80 w-80 rounded-full bg-accent-muted blur-[120px]" />

      <div className="glass-panel relative z-10 w-full max-w-md p-8 text-center sm:p-12 shadow-glow border-accent/20">
        {/* Animated Icon Container */}
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-red-500/10 text-red-500 ring-8 ring-red-500/5 animate-bounce">
          <AlertCircle size={40} className="stroke-[1.75]" />
        </div>

        {/* Heading */}
        <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-dashboard-text">
          Invalid Address
        </h1>
        
        {/* Subtle separator */}
        <div className="mx-auto my-4 h-1 w-12 rounded bg-gradient-to-r from-red-500 to-accent" />

        {/* Error message */}
        <p className="mb-8 text-sm leading-relaxed text-dashboard-muted">
          The page you are looking for does not exist or has been moved to another URL. Let's get you back on track!
        </p>

        {/* Action Button */}
        <Link
          to="/dashboard"
          className="primary-action w-full flex items-center justify-center gap-2 group transition-all duration-300 transform active:scale-95"
        >
          <Home size={18} className="transition-transform group-hover:scale-110" />
          <span>Return to Dashboard</span>
        </Link>
      </div>
    </div>
  );
}