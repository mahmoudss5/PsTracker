export const API_BASE_URL = "http://localhost:8000/api";

export function getHeaders(){
    const token=localStorage.getItem("token")
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    }
} 


