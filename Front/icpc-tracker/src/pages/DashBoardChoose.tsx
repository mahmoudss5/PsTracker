import { getIsCoach } from "../services/AuthService";
import { Navigate } from "react-router-dom";
import{isAuthenticated}from "../services/AuthService"
export function DashBoardChoose() {
    const isCoach=getIsCoach()
      if(!isAuthenticated()){
        return <Navigate to="/auth" replace />
      }
    return (
        <div>
        
            {isCoach?(
                <Navigate to="coach" replace />
            ):(
                <Navigate to="trainee" replace />
            )}
        </div>
    );
}