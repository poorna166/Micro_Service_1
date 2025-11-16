import Link from "next/link";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import LoginForm from "./login-form";

export default function LoginPage() {
  return (
    <div className="flex items-center justify-center min-h-screen bg-muted/40">
       <div className="flex flex-col items-center">
         <Link href="/" className="text-4xl font-headline font-bold text-primary mb-6">
            SkinFlex
          </Link>
        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl font-headline">Login or Sign Up</CardTitle>
            <CardDescription>Enter your mobile number to receive an OTP</CardDescription>
          </CardHeader>
          <CardContent>
            <LoginForm />
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
