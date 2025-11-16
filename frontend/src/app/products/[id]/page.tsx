'use client';

import React, { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import Image from 'next/image';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { Label } from '@/components/ui/label';
import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import { useCart } from '@/context/CartContext';
import { useToast } from '@/hooks/use-toast';
import type { MasterSkin, ProductVariant } from '@/lib/types';
import { cn } from '@/lib/utils';
import { ShoppingCart, CheckCircle, ShieldCheck, Gem, Layers, Plus, Minus } from 'lucide-react';

type ProductPageData = MasterSkin & { brand_name: string };

export default function ProductDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const { cart, addToCart, updateQuantity } = useCart();
  const { toast } = useToast();

  const [product, setProduct] = useState<ProductPageData | null>(null);
  const [selectedVariant, setSelectedVariant] = useState<ProductVariant | null>(null);
  const [activeImage, setActiveImage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (id) {
      const fetchProduct = async () => {
        setIsLoading(true);
        try {
          const response = await fetch(`/api/products/${id}`);
          if (!response.ok) {
            throw new Error('Product not found');
          }
          const data: ProductPageData = await response.json();
          setProduct(data);
          if (data.variants && data.variants.length > 0) {
            const firstVariant = data.variants[0];
            setSelectedVariant(firstVariant);
            if (firstVariant.image_urls && firstVariant.image_urls.length > 0) {
                setActiveImage(firstVariant.image_urls[0]);
            }
          }
        } catch (error) {
          console.error('Failed to fetch product:', error);
        } finally {
          setIsLoading(false);
        }
      };
      fetchProduct();
    }
  }, [id]);

  const handleSelectVariant = (variant: ProductVariant) => {
    setSelectedVariant(variant);
    if (variant.image_urls && variant.image_urls.length > 0) {
        setActiveImage(variant.image_urls[0]);
    }
  }

  const handleAddToCart = () => {
    if (product && selectedVariant) {
      addToCart(product, selectedVariant);
      toast({
        title: 'Added to Cart',
        description: `${selectedVariant.name} has been added to your cart.`,
      });
    }
  };

  const cartItem = selectedVariant && cart.find(item => item.id === selectedVariant.id);

  if (isLoading) {
    return (
        <div className="flex flex-col min-h-screen">
            <AppHeader />
            <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
                <div className="grid md:grid-cols-2 gap-8 lg:gap-12">
                    <div className="space-y-4">
                        <Skeleton className="w-full aspect-square rounded-xl" />
                        <div className="grid grid-cols-5 gap-4">
                            <Skeleton className="w-full aspect-square rounded-lg" />
                            <Skeleton className="w-full aspect-square rounded-lg" />
                            <Skeleton className="w-full aspect-square rounded-lg" />
                            <Skeleton className="w-full aspect-square rounded-lg" />
                            <Skeleton className="w-full aspect-square rounded-lg" />
                        </div>
                    </div>
                    <div className="space-y-6">
                        <Skeleton className="h-8 w-3/4" />
                        <Skeleton className="h-6 w-1/2" />
                        <Skeleton className="h-10 w-1/4" />
                        <div className="flex gap-3">
                            <Skeleton className="w-8 h-8 rounded-full" />
                            <Skeleton className="w-8 h-8 rounded-full" />
                            <Skeleton className="w-8 h-8 rounded-full" />
                        </div>
                        <Skeleton className="h-12 w-full" />
                    </div>
                </div>
            </main>
            <AppFooter />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="flex flex-col min-h-screen">
        <AppHeader />
        <main className="flex-grow container mx-auto px-4 py-8 md:py-12 text-center">
            <h1 className="text-2xl font-bold">Product not found</h1>
            <p className="text-muted-foreground">The product you are looking for does not exist.</p>
        </main>
        <AppFooter />
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen bg-muted/40">
      <AppHeader />
      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="grid md:grid-cols-2 gap-8 lg:gap-16">
          <div>
            <Card className="overflow-hidden shadow-lg mb-4">
              <CardContent className="p-0">
                <Image
                  src={activeImage || 'https://placehold.co/600x600.png'}
                  alt={selectedVariant?.name || 'Product image'}
                  width={600}
                  height={600}
                  className="object-cover w-full h-full aspect-square"
                  priority
                  data-ai-hint="phone skin"
                />
              </CardContent>
            </Card>
            {selectedVariant && selectedVariant.image_urls.length > 1 && (
                <div className="grid grid-cols-5 gap-2">
                    {selectedVariant.image_urls.map((url, index) => (
                    <button
                        key={index}
                        onClick={() => setActiveImage(url)}
                        className={cn(
                            "rounded-lg overflow-hidden border-2 transition-all focus:outline-none",
                            activeImage === url ? "border-primary ring-2 ring-primary ring-offset-2" : "border-transparent hover:border-muted-foreground"
                        )}
                    >
                        <Image
                        src={url}
                        alt={`${selectedVariant.name} thumbnail ${index + 1}`}
                        width={100}
                        height={100}
                        className="object-cover w-full h-full aspect-square"
                        data-ai-hint="phone skin"
                        />
                    </button>
                    ))}
                </div>
            )}
          </div>
          <div className="space-y-6">
            <div>
              <p className="text-sm font-medium text-primary">{product.brand_name.toUpperCase()}</p>
              <h1 className="text-3xl md:text-4xl font-headline font-bold">{product.name}</h1>
              <p className="text-lg text-muted-foreground mt-1">For {product.model_name}</p>
            </div>
            
            {selectedVariant && !cartItem && (
              <div>
                  <p className="text-sm text-muted-foreground">{selectedVariant.name}</p>
                  <p className="text-3xl font-bold mt-2">${selectedVariant.price.toFixed(2)}</p>
              </div>
            )}
            
            <div>
              <Label className="text-base font-semibold">Color</Label>
              <div className="flex gap-3 mt-2">
                {product.variants.map((variant) => (
                  <button
                    key={variant.id}
                    onClick={() => handleSelectVariant(variant)}
                    className={cn(
                      'w-8 h-8 rounded-full border-2 transition-transform hover:scale-110 focus:outline-none',
                      selectedVariant?.id === variant.id ? 'border-primary scale-110 ring-2 ring-offset-2 ring-primary' : 'border-border'
                    )}
                    style={{ backgroundColor: variant.color_hex }}
                    aria-label={`Select ${variant.name}`}
                    data-ai-hint="color swatch"
                  />
                ))}
              </div>
            </div>

            {cartItem ? (
                <div className="flex items-center gap-4 rounded-lg border bg-background p-2">
                    <Button variant="outline" size="icon" className="h-10 w-10" onClick={() => updateQuantity(cartItem.id, cartItem.quantity - 1)}>
                        <Minus className="h-5 w-5" />
                    </Button>
                    <span className="text-xl font-bold w-12 text-center">{cartItem.quantity}</span>
                    <Button variant="outline" size="icon" className="h-10 w-10" onClick={() => updateQuantity(cartItem.id, cartItem.quantity + 1)}>
                        <Plus className="h-5 w-5" />
                    </Button>
                    <div className="ml-auto text-right">
                        <p className="text-muted-foreground text-sm">Total Price</p>
                        <p className="text-2xl font-bold">${(cartItem.price * cartItem.quantity).toFixed(2)}</p>
                    </div>
                </div>
            ) : (
                <Button size="lg" className="w-full h-12 text-lg" onClick={handleAddToCart} disabled={!selectedVariant}>
                    <ShoppingCart className="mr-2 h-5 w-5" />
                    Add to Cart
                </Button>
            )}
            
            <Separator />
            
            <div className="space-y-4 text-sm text-muted-foreground">
                <div className="flex items-center gap-3">
                    <Layers className="w-5 h-5 text-primary" />
                    <span>Premium 3M vinyl for a bubble-free application.</span>
                </div>
                <div className="flex items-center gap-3">
                    <Gem className="w-5 h-5 text-primary" />
                    <span>Unique, high-resolution prints you won't find anywhere else.</span>
                </div>
                <div className="flex items-center gap-3">
                    <ShieldCheck className="w-5 h-5 text-primary" />
                    <span>Protects your device from everyday scratches and scuffs.</span>
                </div>
                 <div className="flex items-center gap-3">
                    <CheckCircle className="w-5 h-5 text-primary" />
                    <span>Easy to apply and remove without residue.</span>
                </div>
            </div>
          </div>
        </div>
      </main>
      <AppFooter />
    </div>
  );
}
