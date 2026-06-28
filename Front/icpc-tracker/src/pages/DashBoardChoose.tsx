import { getIsCoach } from "../services/AuthService";
import { Navigate } from "react-router-dom";
export function DashBoardChoose() {
    const isCoach=getIsCoach()
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