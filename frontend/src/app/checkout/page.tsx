
'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import Script from 'next/script';
import axios from 'axios';

import { useCart } from '@/context/CartContext';
import { useToast } from '@/hooks/use-toast';
import { useAuth } from '@/context/AuthContext';

import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";


const shippingSchema = z.object({
  name: z.string().min(2, { message: "Name must be at least 2 characters." }),
  email: z.string().email({ message: "Please enter a valid email." }),
  address: z.string().min(5, { message: "Please enter a valid address." }),
  city: z.string().min(2, { message: "Please enter a valid city." }),
  state: z.string().min(2, { message: "Please enter a valid state." }),
  zip: z.string().min(5, { message: "Please enter a valid ZIP code." }),
});

export default function CheckoutPage() {
    const { cart, cartTotal, clearCart } = useCart();
    const { user } = useAuth();
    const { toast } = useToast();
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(false);

    const form = useForm<z.infer<typeof shippingSchema>>({
        resolver: zodResolver(shippingSchema),
        defaultValues: {
            name: "",
            email: "",
            address: "",
            city: "",
            state: "",
            zip: "",
        },
    });

    const onSubmit = async (values: z.infer<typeof shippingSchema>) => {
        setIsLoading(true);
        
        // This is where you would integrate Razorpay.
        // The following code is a commented-out guide.
        // You will need to implement the corresponding backend API routes.
        
        /*
        // Step 1: Create an order on your backend.
        // Your backend will use the Razorpay SDK to create an order and return the order details.
        try {
            const { data: order } = await axios.post('/api/create-razorpay-order', {
                amount: cartTotal * 100, // Amount in paise
                currency: 'INR',
            });

            // Step 2: Open the Razorpay checkout modal.
            const options = {
                key: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID,
                amount: order.amount,
                currency: order.currency,
                name: "SkinFlex",
                description: "Phone Skin Purchase",
                image: "/logo.png", // Add a logo in your public folder
                order_id: order.id,
                handler: async function (response: any) {
                    // Step 3: Verify the payment on your backend.
                    try {
                        await axios.post('/api/verify-razorpay-payment', {
                            razorpay_order_id: response.razorpay_order_id,
                            razorpay_payment_id: response.razorpay_payment_id,
                            razorpay_signature: response.razorpay_signature,
                        });
                        
                        // If verification is successful, complete the order process.
                        toast({
                            title: "Order Placed Successfully!",
                            description: "Thank you for your purchase. We'll process it shortly.",
                        });
                        clearCart();
                        router.push('/account/orders');

                    } catch (error) {
                        console.error("Payment verification failed", error);
                        toast({
                            variant: "destructive",
                            title: "Payment Failed",
                            description: "Your payment could not be verified. Please contact support.",
                        });
                        setIsLoading(false);
                    }
                },
                prefill: {
                    name: values.name,
                    email: values.email,
                    contact: user?.phoneNumber,
                },
                notes: {
                    address: `${values.address}, ${values.city}, ${values.state} - ${values.zip}`,
                },
                theme: {
                    color: "#330066",
                },
            };
            
            // @ts-ignore
            const rzp = new window.Razorpay(options);
            rzp.on('payment.failed', function (response: any) {
                toast({
                    variant: "destructive",
                    title: "Payment Failed",
                    description: response.error.description,
                });
                setIsLoading(false);
            });
            rzp.open();

        } catch (error) {
            console.error("Error creating Razorpay order", error);
            toast({
                variant: "destructive",
                title: "Error",
                description: "Could not initiate payment. Please try again.",
            });
            setIsLoading(false);
        }
        */

        // Placeholder logic for when Razorpay is commented out
        console.log("Placing order with shipping details:", values);
        setTimeout(() => {
            toast({
                title: "Order Placed Successfully! (Mock)",
                description: "This is a mock order. No payment was processed.",
            });
            clearCart();
            setIsLoading(false);
            router.push('/account/orders');
        }, 1500);
    };

    if (cart.length === 0 && !isLoading) {
        return (
            <div className="flex flex-col min-h-screen bg-muted/40">
                <AppHeader />
                <main className="flex-grow container mx-auto px-4 py-8 md:py-12 flex items-center justify-center">
                    <Card className="w-full max-w-md">
                        <CardContent className="pt-6">
                            <div className="flex flex-col items-center gap-4 py-16 text-center">
                                <h2 className="text-2xl font-semibold">Your cart is empty</h2>
                                <p className="max-w-xs text-muted-foreground">Add items to your cart to proceed to checkout.</p>
                                <Button asChild className="mt-4">
                                    <Link href="/products">Start Shopping</Link>
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                </main>
                <AppFooter />
            </div>
        )
    }

    return (
        <>
            {/* This script loads the Razorpay checkout library */}
            <Script
                id="razorpay-checkout-js"
                src="https://checkout.razorpay.com/v1/checkout.js"
            />
            <div className="flex flex-col min-h-screen bg-muted/40">
                <AppHeader />
                <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
                    <div className="mb-8 text-center">
                        <h1 className="text-3xl md:text-4xl font-headline font-bold">Checkout</h1>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
                        {/* Shipping Form */}
                        <Card>
                            <CardHeader>
                                <CardTitle>Shipping & Contact</CardTitle>
                                <CardDescription>Enter the address where you'd like to receive your order.</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <Form {...form}>
                                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                                        <FormField control={form.control} name="name" render={({ field }) => (
                                            <FormItem><FormLabel>Full Name</FormLabel><FormControl><Input placeholder="John Doe" {...field} /></FormControl><FormMessage /></FormItem>
                                        )} />
                                        <FormField control={form.control} name="email" render={({ field }) => (
                                            <FormItem><FormLabel>Email Address</FormLabel><FormControl><Input type="email" placeholder="you@example.com" {...field} /></FormControl><FormMessage /></FormItem>
                                        )} />
                                        <FormField control={form.control} name="address" render={({ field }) => (
                                            <FormItem><FormLabel>Street Address</FormLabel><FormControl><Input placeholder="123 Tech Lane" {...field} /></FormControl><FormMessage /></FormItem>
                                        )} />
                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                            <FormField control={form.control} name="city" render={({ field }) => (
                                                <FormItem className="md:col-span-1"><FormLabel>City</FormLabel><FormControl><Input placeholder="Silicon Valley" {...field} /></FormControl><FormMessage /></FormItem>
                                            )} />
                                            <FormField control={form.control} name="state" render={({ field }) => (
                                                <FormItem><FormLabel>State / Province</FormLabel><FormControl><Input placeholder="CA" {...field} /></FormControl><FormMessage /></FormItem>
                                            )} />
                                            <FormField control={form.control} name="zip" render={({ field }) => (
                                                <FormItem><FormLabel>ZIP / Postal</FormLabel><FormControl><Input placeholder="94043" {...field} /></FormControl><FormMessage /></FormItem>
                                            )} />
                                        </div>
                                        <Button type="submit" className="w-full mt-6" size="lg" disabled={isLoading}>
                                            {isLoading ? "Processing..." : `Pay $${cartTotal.toFixed(2)}`}
                                        </Button>
                                    </form>
                                </Form>
                            </CardContent>
                        </Card>

                        {/* Order Summary */}
                        <div className="sticky top-24">
                            <Card>
                                <CardHeader>
                                    <CardTitle>Order Summary</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="space-y-4">
                                        {cart.map(item => (
                                            <div key={item.id} className="flex justify-between items-center">
                                                <div className="flex items-center gap-4">
                                                    <Image src={item.image_urls[0]} alt={item.name} width={64} height={64} className="rounded-md border object-cover" data-ai-hint="phone skin" />
                                                    <div>
                                                        <p className="font-semibold">{item.name}</p>
                                                        <p className="text-sm text-muted-foreground">Qty: {item.quantity}</p>
                                                    </div>
                                                </div>
                                                <p className="font-semibold">${(item.price * item.quantity).toFixed(2)}</p>
                                            </div>
                                        ))}
                                    </div>
                                    <Separator className="my-4" />
                                    <div className="space-y-2">
                                        <div className="flex justify-between">
                                            <span>Subtotal</span>
                                            <span className="font-semibold">${cartTotal.toFixed(2)}</span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>Shipping</span>
                                            <span>Free</span>
                                        </div>
                                        <Separator className="my-4" />
                                        <div className="flex justify-between text-lg font-bold">
                                            <span>Total</span>
                                            <span>${cartTotal.toFixed(2)}</span>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </div>
                </main>
                <AppFooter />
            </div>
        </>
    );
}
