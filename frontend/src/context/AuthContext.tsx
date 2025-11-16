'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { onAuthStateChanged, User, signOut, getRedirectResult } from 'firebase/auth';
import { auth } from '@/lib/firebase';
import { useRouter } from 'next/navigation';
import { useToast } from '@/hooks/use-toast';

interface AuthContextType {
  user: User | null;
  isAdmin: boolean;
  loading: boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const adminPhoneNumber = process.env.NEXT_PUBLIC_ADMIN_PHONE_NUMBER;
const adminEmail = process.env.NEXT_PUBLIC_ADMIN_EMAIL;

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [isAdmin, setIsAdmin] = useState(false);
  const router = useRouter();
  const { toast } = useToast();

  useEffect(() => {
    // If Firebase isn't configured, don't attempt to use it.
    if (!auth) {
        setLoading(false);
        return;
    }

    // First, check for redirect result from an OAuth provider
    getRedirectResult(auth)
      .then((result) => {
        if (result) {
          // This means a user has just signed in via redirect.
          const user = result.user;
          toast({
            title: "Login Successful",
            description: `Welcome, ${user.displayName || 'user'}!`,
          });
          
          const isEmailAdmin = adminEmail && user.email === adminEmail;
          if (isEmailAdmin) {
            router.push("/admin");
          } else {
            router.push("/account/orders");
          }
        }
      }).catch((error) => {
        console.error("Google sign-in redirect error", error);
        toast({
            variant: "destructive",
            title: "Google Sign-In Failed",
            description: "An error occurred during sign-in. Please try again.",
        });
      });
      
    // Then, set up the persistent auth state listener
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      if (user) {
        setUser(user);
        const isPhoneAdmin = adminPhoneNumber && user.phoneNumber === adminPhoneNumber;
        const isEmailAdmin = adminEmail && user.email === adminEmail;
        setIsAdmin(isPhoneAdmin || isEmailAdmin);
      } else {
        setUser(null);
        setIsAdmin(false);
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, [router, toast]);

  const logout = async () => {
    if (auth) {
      await signOut(auth);
    }
    setUser(null);
    setIsAdmin(false);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{ user, isAdmin, loading, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
