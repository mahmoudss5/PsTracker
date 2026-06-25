
import { getIsCoach } from "./AuthService";

export function isUserCoach():boolean{
  const isCoach=getIsCoach()
  return isCoach===true ? true : false
} 