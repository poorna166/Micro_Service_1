import React from 'react';
import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { orders } from '@/lib/mock-data';
import type { Order } from '@/lib/types';
import Image from 'next/image';
import Link from 'next/link';
import { ShoppingBag } from 'lucide-react';

export default function OrderHistoryPage() {
  // Assuming a logged-in user with ID 1 for mock purposes
  const userOrders = orders.filter(order => order.user_id === 1);

  const getShippingStatusVariant = (status: Order['shippingStatus']): "default" | "secondary" | "outline" | "destructive" => {
    switch (status) {
      case 'Delivered':
        return 'default';
      case 'Shipped':
        return 'outline';
      case 'Processing':
        return 'secondary';
      case 'Cancelled':
        return 'destructive';
      default:
        return 'secondary';
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-muted/40">
      <AppHeader />
      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="mb-8">
            <h1 className="text-3xl md:text-4xl font-headline font-bold">My Orders</h1>
            <p className="text-muted-foreground">Track and manage your past purchases.</p>
        </div>

        {userOrders.length > 0 ? (
          <div className="space-y-8">
            {userOrders.map((order) => (
              <Card key={order.id} className="overflow-hidden shadow-md hover:shadow-lg transition-shadow duration-300">
                <CardHeader className="bg-card p-4 md:p-6">
                  <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div>
                      <CardTitle className="text-xl font-semibold">Order #{order.id}</CardTitle>
                      <CardDescription>Placed on {new Date(order.date).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</CardDescription>
                    </div>
                    <div className="flex items-center gap-4">
                      <Badge variant={getShippingStatusVariant(order.shippingStatus)} className="py-1 px-3 text-sm font-medium">{order.shippingStatus}</Badge>
                      <p className="text-xl font-bold">${order.total.toFixed(2)}</p>
                    </div>
                  </div>
                </CardHeader>
                <CardContent className="p-4 md:p-6">
                  <div className="space-y-4">
                    {order.items.map((item, index) => (
                      <React.Fragment key={index}>
                        <div className="flex items-start sm:items-center gap-4 flex-col sm:flex-row">
                          <Image
                            src={item.product.image_urls[0]}
                            alt={item.product.name}
                            width={80}
                            height={80}
                            className="rounded-lg border aspect-square object-cover"
                            data-ai-hint="phone skin"
                          />
                          <div className="flex-grow">
                            <p className="font-semibold text-lg">{item.product.name}</p>
                            <p className="text-sm text-muted-foreground">{item.product.master_name} for {item.product.model_name}</p>
                            <p className="text-sm text-muted-foreground">Qty: {item.quantity}</p>
                          </div>
                          <p className="font-semibold text-lg sm:ml-auto">${(item.product.price * item.quantity).toFixed(2)}</p>
                        </div>
                        {index < order.items.length - 1 && <Separator className="my-4" />}
                      </React.Fragment>
                    ))}
                  </div>
                </CardContent>
                <CardFooter className="bg-muted/50 p-4 md:p-6 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div className="text-sm">
                        <p className="font-semibold">Shipping to:</p>
                        <p className="text-muted-foreground">{order.shippingAddress.name}</p>
                        <p className="text-muted-foreground">{order.shippingAddress.address}</p>
                        <p className="text-muted-foreground">{order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.zip}</p>
                    </div>
                    <div className="flex gap-2 self-end md:self-center">
                        <Button variant="outline">View Invoice</Button>
                        <Button>Track Order</Button>
                    </div>
                </CardFooter>
              </Card>
            ))}
          </div>
        ) : (
          <Card>
            <CardContent className="pt-6">
                <div className="flex flex-col items-center gap-4 py-12 text-center">
                    <ShoppingBag className="w-16 h-16 text-muted-foreground/50" />
                    <h2 className="text-2xl font-semibold">No orders yet</h2>
                    <p className="max-w-xs text-muted-foreground">Looks like you haven&apos;t made any purchases. Let&apos;s change that!</p>
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
