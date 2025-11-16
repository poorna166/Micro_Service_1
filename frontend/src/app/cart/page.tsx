
'use client';

import React from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useCart } from '@/context/CartContext';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Separator } from '@/components/ui/separator';
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from '@/components/ui/card';
import { Trash2, Plus, Minus, ShoppingCart } from 'lucide-react';
import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import { useToast } from '@/hooks/use-toast';

export default function CartPage() {
  const { cart, updateQuantity, removeFromCart, cartTotal } = useCart();
  const { toast } = useToast();

  const handleRemoveItem = (variantId: number, variantName: string) => {
    removeFromCart(variantId);
    toast({
      title: 'Item Removed',
      description: `${variantName} has been removed from your cart.`,
    });
  };

  const handleQuantityChange = (variantId: number, newQuantity: number) => {
    if (newQuantity >= 1) {
      updateQuantity(variantId, newQuantity);
    } else {
        removeFromCart(variantId);
        toast({
            title: 'Item Removed',
            description: `An item has been removed from your cart.`,
        });
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-muted/40">
      <AppHeader />
      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="mb-8">
            <h1 className="text-3xl md:text-4xl font-headline font-bold">Your Shopping Cart</h1>
        </div>

        {cart.length > 0 ? (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
            <div className="lg:col-span-2">
              <Card>
                <CardContent className="p-0">
                  <div className="divide-y divide-border">
                    {cart.map(item => (
                      <div key={item.id} className="flex items-center gap-4 p-4 md:p-6">
                        <Image
                          src={item.image_urls[0]}
                          alt={item.name}
                          width={100}
                          height={100}
                          className="rounded-lg border aspect-square object-cover"
                          data-ai-hint="phone skin"
                        />
                        <div className="flex-grow">
                          <p className="font-semibold text-lg">{item.name}</p>
                          <p className="text-sm text-muted-foreground">{item.master_name} for {item.model_name}</p>
                          <p className="text-lg font-bold text-primary mt-1">${item.price.toFixed(2)}</p>
                        </div>
                        <div className="flex items-center gap-2">
                          <Button 
                            variant="outline" 
                            size="icon" 
                            className="h-8 w-8"
                            onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                          >
                            <Minus className="h-4 w-4" />
                          </Button>
                          <Input
                            type="number"
                            value={item.quantity}
                            onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value, 10) || 1)}
                            className="w-16 h-8 text-center"
                            min="1"
                          />
                           <Button 
                            variant="outline" 
                            size="icon" 
                            className="h-8 w-8"
                            onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                          >
                            <Plus className="h-4 w-4" />
                          </Button>
                        </div>
                        <Button variant="ghost" size="icon" className="text-muted-foreground hover:text-destructive" onClick={() => handleRemoveItem(item.id, item.name)}>
                          <Trash2 className="h-5 w-5" />
                        </Button>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
            
            <div className="lg:col-span-1">
              <Card className="sticky top-24">
                <CardHeader>
                  <CardTitle className="text-2xl font-headline">Order Summary</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex justify-between">
                    <span>Subtotal</span>
                    <span className="font-semibold">${cartTotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Shipping</span>
                    <span className="font-semibold">Free</span>
                  </div>
                  <Separator />
                  <div className="flex justify-between text-xl font-bold">
                    <span>Total</span>
                    <span>${cartTotal.toFixed(2)}</span>
                  </div>
                </CardContent>
                <CardFooter>
                  <Button size="lg" className="w-full" asChild>
                    <Link href="/checkout">Proceed to Checkout</Link>
                  </Button>
                </CardFooter>
              </Card>
            </div>
          </div>
        ) : (
          <Card>
             <CardContent className="pt-6">
                <div className="flex flex-col items-center gap-4 py-16 text-center">
                    <ShoppingCart className="w-20 h-20 text-muted-foreground/50" />
                    <h2 className="text-2xl font-semibold">Your cart is empty</h2>
                    <p className="max-w-xs text-muted-foreground">Looks like you haven&apos;t added anything to your cart yet.</p>
                    <Button asChild className="mt-4">
                        <Link href="/products">Start Shopping</Link>
                    </Button>
                </div>
            </CardContent>
          </Card>
        )}
      </main>
      <AppFooter />
    </div>
  );
}
