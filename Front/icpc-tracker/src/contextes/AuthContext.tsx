import {createContext,useContext,useMemo} from 'react'
import  type { AuthContextType} from '../types/auth.types'
import {useMutation,useQueryClient} from '@tanstack/react-query'


export  const AuthContext = createContext<AuthContextType|undefined>(undefined)

export function AuthProvider({children}: {children: React.ReactNode}) {

    const queryClient=useQueryClient();









return(
    <AuthContext.Provider value={}>
      {children}
    </AuthContext.Provider>
)




}

export function useAuth():AuthContextType{
  const context=useContext(AuthContext);
  if(!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
}







