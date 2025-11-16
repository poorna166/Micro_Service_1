'use client';

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";
import { useRouter } from "next/navigation";
import { auth } from "@/lib/firebase";
import { RecaptchaVerifier, signInWithPhoneNumber, type ConfirmationResult, GoogleAuthProvider, signInWithRedirect } from "firebase/auth";
import { Loader2 } from "lucide-react";

declare global {
  interface Window {
    recaptchaVerifier?: RecaptchaVerifier;
    confirmationResult?: ConfirmationResult;
  }
}

const GoogleIcon = () => (
    <svg role="img" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2">
        <title>Google</title>
        <path d="M12.48 10.92v3.28h7.84c-.24 1.84-.85 3.18-1.73 4.1-1.02 1.02-2.62 2.04-4.75 2.04-5.52 0-9.92-4.48-9.92-9.92s4.4-9.92 9.92-9.92c2.82 0 4.95 1.16 6.56 2.68l2.52-2.52C19.23 3.48 16.36 2 12.48 2 5.6 2 0 7.6 0 14.4s5.6 12.4 12.48 12.4c6.92 0 12.08-4.82 12.08-12.4 0-1.12-.12-2.2-.32-3.28z" fill="#4285F4"></path>
        <path d="m22.42 14.2-2.52 2.52c-1.6 1.52-3.74 2.68-6.56 2.68-5.52 0-9.92-4.48-9.92-9.92s4.4-9.92 9.92-9.92c2.82 0 4.95 1.16 6.56 2.68l2.52-2.52C19.23 3.48 16.36 2 12.48 2 5.6 2 0 7.6 0 14.4s5.6 12.4 12.48 12.4c6.92 0 12.08-4.82 12.08-12.4 0-1.12-.12-2.2-.32-3.28H12.48v3.28h9.94z" fill="#34A853"></path>
        <path d="M22.42 14.2v-3.28H12.48v3.28z" fill="#FBBC05"></path>
        <path d="M22.42 14.2c-.2.96-.44 1.92-.76 2.8l-2.56-2.56.04-.24h3.28z" fill="#EA4335"></path>
    </svg>
);


export default function LoginForm() {
  const [step, setStep] = useState(1); // 1 for phone number, 2 for OTP
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isGoogleLoading, setIsGoogleLoading] = useState(false);
  const { toast } = useToast();
  const router = useRouter();

  useEffect(() => {
    if (!auth) {
        // If firebase is not configured, don't initialize recaptcha
        return;
    }
    if (step === 1 && !window.recaptchaVerifier) {
      window.recaptchaVerifier = new RecaptchaVerifier(auth, 'recaptcha-container', {
        'size': 'invisible',
        'callback': (response: any) => {
          // reCAPTCHA solved.
        }
      });
    }
  }, [step]);


  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    if (!auth) {
        toast({
            variant: "destructive",
            title: "Configuration Error",
            description: "Firebase is not configured correctly.",
        });
        setIsLoading(false);
        return;
    }

    if (!window.recaptchaVerifier) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "reCAPTCHA not initialized. Please refresh.",
      });
      setIsLoading(false);
      return;
    }
    
    try {
      const appVerifier = window.recaptchaVerifier;

      const formattedPhone = `+91${phone.trim()}`;
const confirmationResult = await signInWithPhoneNumber(auth, formattedPhone, appVerifier);

     // const confirmationResult = await signInWithPhoneNumber(auth, phone, appVerifier);
      window.confirmationResult = confirmationResult;
      setIsLoading(false);
      setStep(2);
      toast({
        title: "OTP Sent",
        description: "An OTP has been sent to your mobile number.",
      });
    } catch (error: any) {
      setIsLoading(false);
      toast({
        variant: "destructive",
        title: "Failed to send OTP",
        description: "Please use a valid phone number in international format (e.g. +91555555555).",
      });
      
      // @ts-ignore
      if (window.grecaptcha && window.recaptchaVerifier) {
        window.recaptchaVerifier.render().then((widgetId) => {
          // @ts-ignore
          window.grecaptcha.reset(widgetId);
        });
      }
    }
  };

  const handleVerifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    if (!auth) {
        toast({
            variant: "destructive",
            title: "Configuration Error",
            description: "Firebase is not configured correctly.",
        });
        setIsLoading(false);
        return;
    }

    if (!window.confirmationResult) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Verification session expired. Please try again.",
      });
      setIsLoading(false);
      setStep(1);
      return;
    }

    try {
      const result = await window.confirmationResult.confirm(otp);
      const user = result.user;
      setIsLoading(false);
      toast({
        title: "Login Successful",
        description: "Welcome back!",
      });
      
      const adminPhoneNumber = process.env.NEXT_PUBLIC_ADMIN_PHONE_NUMBER;
      if (adminPhoneNumber && user.phoneNumber === adminPhoneNumber) {
        router.push("/admin");
      } else {
        router.push("/account/orders");
      }
    } catch (error: any) {
      setIsLoading(false);
      toast({
        variant: "destructive",
        title: "Login Failed",
        description: "Invalid OTP. Please try again.",
      });
    }
  };

  const handleGoogleSignIn = async () => {
    if (!auth) {
        toast({
            variant: "destructive",
            title: "Configuration Error",
            description: "Firebase is not configured correctly.",
        });
        return;
    }
    setIsGoogleLoading(true);
    const provider = new GoogleAuthProvider();
    try {
        // This will redirect the user away from the app. 
        // The result is handled in AuthContext after they return.
        await signInWithRedirect(auth, provider);
    } catch (error: any) {
        toast({
            variant: "destructive",
            title: "Google Sign-In Failed",
            description: "Could not start the sign-in process. Please try again.",
        });
        setIsGoogleLoading(false);
    }
  };


  return (
    <div>
      <div id="recaptcha-container"></div>
      {step === 1 && (
        <form onSubmit={handleSendOtp} className="space-y-4">
         <div className="space-y-2">
  <Label htmlFor="phone">Mobile Number</Label>
  <div className="flex items-center border rounded-md px-3 py-2">
    <span className="text-gray-500 mr-2">+91</span>
    <input
      id="phone"
      type="tel"
      placeholder="**********"
      value={phone}
      onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))}
      className="flex-1 outline-none bg-transparent"
      required
    />
  </div>
</div>
          <Button type="submit" className="w-full" disabled={isLoading || !auth}>
            {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {isLoading ? "Sending OTP..." : "Send OTP"}
          </Button>
        </form>
      )}

      {step === 2 && (
        <form onSubmit={handleVerifyOtp} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="otp">Enter OTP</Label>
            <Input
              id="otp"
              type="text"
              placeholder="Enter the 6-digit code"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
              maxLength={6}
            />
          </div>
           <Button type="submit" className="w-full" disabled={isLoading || !auth}>
            {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {isLoading ? "Verifying..." : "Verify OTP & Login"}
          </Button>
          <Button variant="link" onClick={() => { setStep(1); setIsLoading(false); }} className="w-full">
            Back to phone number
          </Button>
        </form>
      )}

      {step === 1 && (
        <>
            <div className="relative my-6">
                <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-card px-2 text-muted-foreground">Or continue with</span>
                </div>
            </div>
            <Button variant="outline" className="w-full" onClick={handleGoogleSignIn} disabled={isGoogleLoading || !auth}>
                {isGoogleLoading ? (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                    <GoogleIcon />
                )}
                Sign in with Google
            </Button>
        </>
      )}
    </div>
  );
}
