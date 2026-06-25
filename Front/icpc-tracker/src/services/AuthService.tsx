import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { jwtDecode } from "jwt-decode";
import type { LoginCredentials,RegisterCredentials } from "../types/auth.types";
import { normalizeApiError } from "./ErrorService";

export function storeUserId(userId:number){
    localStorage.setItem("userId",JSON.stringify(userId))
}

export function getUserId(){
    return JSON.parse(localStorage.getItem("userId") as string)
}
export function storeUserNmae(userName:string){
    localStorage.setItem("userName",JSON.stringify(userName))
}

export function getUserName(){
    return JSON.parse(localStorage.getItem("userName") as string)
}

export function storeToken(token:string){
    localStorage.setItem("token",token)
}

export function getToken(){
    return localStorage.getItem("token")
}

export function storeIsCoach(isCoach:boolean){
    localStorage.setItem("isCoach",JSON.stringify(isCoach))
}

export function getIsCoach(){
    return JSON.parse(localStorage.getItem("isCoach") as string)
}


export function removeUser(){
    localStorage.removeItem("userId")
    localStorage.removeItem("userName")
    localStorage.removeItem("token")
    localStorage.removeItem("isCoach")
}


export async function login(credentials:LoginCredentials){
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/login`,credentials)
        storeUserId(response.data.user.id)
        storeUserNmae(response.data.user.userName)
        storeToken(response.data.user.token)
        storeIsCoach(response.data.user.isCoach)
    } catch (error) {
      throw normalizeApiError(error,"Failed to login")
    }
}


export async function register(credentials:RegisterCredentials){
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/register`,credentials)
        storeUserId(response.data.user.id)
        storeUserNmae(response.data.user.userName)
        storeToken(response.data.user.token)
        storeIsCoach(response.data.user.isCoach)
    } catch (error) {
      throw normalizeApiError(error,"Failed to register")
    }
}