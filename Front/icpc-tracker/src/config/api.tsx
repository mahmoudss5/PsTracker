import axios from "axios";

export const apiClient=axios.create({
    baseURL:"http://localhost:8080/api",
    headers:{
        "Content-Type": "application/json",
    },
    withCredentials:true
})


apiClient.interceptors.request.use((config)=>{
    const token=localStorage.getItem("token")
    if(token){
        config.headers.Authorization=`Bearer ${token}`
    }
    return config
},
(error:Error)=>{
    return Promise.reject(error)
}
)

let isRefreshing:boolean=false
let failedQueue:any[]=[]

const processQueue=(error:any ,token :string| null)=>{
    failedQueue.forEach((request)=>{
        if(error){
            request.reject(error)
        }else{
            request.resolve(token)
        }
    })
    failedQueue=[]
}


apiClient.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (originalRequest.url === "/auth/refresh") {
        localStorage.clear();
        return Promise.reject(error);
      }

      originalRequest._retry = true;

      if (isRefreshing) {
        return new Promise<string | null>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      isRefreshing = true;

      try {
        const response = await apiClient.post("/auth/refresh");

        const { token: newAccessToken } = response.data;

        localStorage.setItem("token", newAccessToken);

        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        processQueue(null, newAccessToken);

        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);

        localStorage.clear();
        window.location.href = "/login";

        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);