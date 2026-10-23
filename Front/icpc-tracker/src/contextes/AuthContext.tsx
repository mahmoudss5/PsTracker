import { useMemo } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import type { LoginCredentials, RegisterCredentials } from '../types/auth.types'
import { login, register, logout } from '../services/AuthService'
import { AuthContext } from './authContext'

export function AuthProvider({ children }: { children: React.ReactNode }) {

    const queryClient = useQueryClient();

    const loginMutation = useMutation({
        mutationFn: async (credentials: LoginCredentials) => {
            await login(credentials)
        }
    })

    const registerMutation = useMutation({
        mutationFn: async (credentials: RegisterCredentials) => {
            await register(credentials)
        }
    })

    const value = useMemo(() => {
        // Derive a single error message from whichever mutation last failed
        const isError: string | undefined =
            (loginMutation.error as Error | null)?.message ??
            (registerMutation.error as Error | null)?.message ??
            undefined;

        return {
            isError,
            login: async (credentials: LoginCredentials) => {
                await loginMutation.mutateAsync(credentials);
            },
            register: async (credentials: RegisterCredentials) => {
                await registerMutation.mutateAsync(credentials);
            },
            logout: async () => {
                await logout();
                queryClient.clear();
            },
        };
    }, [loginMutation, registerMutation, queryClient])


return(
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
)




}







