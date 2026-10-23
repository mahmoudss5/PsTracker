import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import { apiBaseUrl } from "./runtime";

type RetryableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

interface QueuedRequest {
  resolve: (token: string | null) => void;
  reject: (error: unknown) => void;
}

export const apiClient=axios.create({
  baseURL: apiBaseUrl,
    headers:{
        "Content-Type": "application/json",
    },
    withCredentials:true
})


apiClient.interceptors.request.use((config) => {
    const token=localStorage.getItem("token")
    if(token){
        config.headers.Authorization=`Bearer ${token}`
    }
    return config
},
(error: unknown) => {
    return Promise.reject(error)
}
)

let isRefreshing:boolean=false
let failedQueue: QueuedRequest[] = [];

const processQueue = (error: unknown, token: string | null): void => {
    failedQueue.forEach((request)=>{
        if(error){
            request.reject(error)
        }else{
            request.resolve(token)
        }
    })
    failedQueue=[]
};


apiClient.interceptors.response.use(
  (response) => response,

  async (error: unknown) => {
    if (!axios.isAxiosError(error)) {
      return Promise.reject(error);
    }

    const axiosError = error as AxiosError;
    const originalRequest = axiosError.config as RetryableRequestConfig | undefined;
    if (!originalRequest) {
      return Promise.reject(error);
    }

    if ((axiosError.response?.status === 401 || axiosError.response?.status === 403) && !originalRequest._retry) {
      // If the original request was login or register, just pass the 401 error through
      // so the form can display the "bad credentials" error instead of trying to refresh.
      if (originalRequest.url === "/auth/login" || originalRequest.url === "/auth/register") {
        return Promise.reject(axiosError);
      }

      if (originalRequest.url === "/auth/refresh") {
        localStorage.clear();
        return Promise.reject(axiosError);
      }

      originalRequest._retry = true;

      if (isRefreshing) {
        return new Promise<string | null>((resolve, reject) => {
          failedQueue.push({
            resolve: (token) => resolve(token),
            reject,
          });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      isRefreshing = true;

      try {
        const response = await apiClient.get<{ token: string }>("/auth/refresh");

        const { token: newAccessToken } = response.data;

        localStorage.setItem("token", newAccessToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        processQueue(null, newAccessToken);

        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);

        localStorage.clear();
        window.location.href = "/auth";

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(axiosError);
  }
);
