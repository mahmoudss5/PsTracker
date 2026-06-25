import axios from "axios";
export function normalizeApiError(error:unknown,fallbackMessage="Something went wrong"):Error{
 
    if(axios.isAxiosError(error)){
     
        const status=error.response?.status
        const data = error.response?.data;

        let message=fallbackMessage


        if(data?.message){
            message=data.message
        }
        if(error.request && !error.response){
            message="Network error, please try again or check connection"
        }
        
        const customeError=new Error(message)
        customeError.name=status? `ApiError:${status}`:'apiError'

        return customeError
    }
    
    if(error instanceof Error){
        return error
    }
    return new Error(fallbackMessage)

}