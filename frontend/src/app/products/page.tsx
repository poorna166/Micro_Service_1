'use client';

import React, { useState, useEffect } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { Card, CardContent } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import { brands, models } from '@/lib/mock-data';
import type { MasterSkin, Brand, PhoneModel, ProductVariant } from '@/lib/types';
import { useToast } from '@/hooks/use-toast';
import { PackageSearch, Search, ShoppingCart, Plus, Minus } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { useCart } from '@/context/CartContext';

export default function ProductsPage() {
  const [allProducts, setAllProducts] = useState<MasterSkin[]>([]);
  const [filteredProducts, setFilteredProducts] = useState<MasterSkin[]>([]);
  const [selectedBrands, setSelectedBrands] = useState<number[]>([]);
  const [selectedModels, setSelectedModels] = useState<number[]>([]);
  const [selectedColors, setSelectedColors] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const { toast } = useToast();
  const { cart, addToCart, updateQuantity } = useCart();

  useEffect(() => {
    const fetchProducts = async () => {
      setIsLoading(true);
      try {
        const response = await fetch('/api/products');
        if (!response.ok) throw new Error('Failed to fetch products');
        const data: MasterSkin[] = await response.json();
        setAllProducts(data);
        setFilteredProducts(data);
      } catch (error) {
        console.error(error);
        toast({
          variant: 'destructive',
          title: 'Error fetching products',
          description: 'Could not load products. Please try again later.',
        });
      } finally {
        setIsLoading(false);
      }
    };
    fetchProducts();
  }, [toast]);
  
  const colorOptions = React.useMemo(() => {
    const colors = new Map<string, string>();
    allProducts.forEach(product => {
      product.variants.forEach(variant => {
        if (!colors.has(variant.color_hex)) {
          colors.set(variant.color_hex, variant.name);
        }
      });
    });
    return Array.from(colors.entries()).map(([hex, name]) => ({ hex, name }));
  }, [allProducts]);

  useEffect(() => {
    let tempProducts = [...allProducts];

    if (searchQuery) {
      const lowercasedQuery = searchQuery.toLowerCase();
      tempProducts = tempProducts.filter(
        p =>
          p.name.toLowerCase().includes(lowercasedQuery) ||
          p.model_name.toLowerCase().includes(lowercasedQuery) ||
          p.variants.some(v => v.name.toLowerCase().includes(lowercasedQuery))
      );
    }

    if (selectedBrands.length > 0) {
      const brandModelIds = models.filter(m => selectedBrands.includes(m.brand_id)).map(m => m.id);
      tempProducts = tempProducts.filter(p => brandModelIds.includes(p.model_id));
    }

    if (selectedModels.length > 0) {
      tempProducts = tempProducts.filter(p => selectedModels.includes(p.model_id));
    }

    if (selectedColors.length > 0) {
      tempProducts = tempProducts.filter(p =>
        p.variants.some(v => selectedColors.includes(v.color_hex))
      );
    }

    setFilteredProducts(tempProducts);
  }, [selectedBrands, selectedModels, selectedColors, searchQuery, allProducts]);

  const handleBrandChange = (brandId: number) => {
    setSelectedBrands(prev => (prev.includes(brandId) ? prev.filter(id => id !== brandId) : [...prev, brandId]));
    setSelectedModels([]);
  };

  const handleModelChange = (modelId: number) => {
    setSelectedModels(prev => (prev.includes(modelId) ? prev.filter(id => id !== modelId) : [...prev, modelId]));
  };
  
  const handleColorChange = (colorHex: string) => {
    setSelectedColors(prev => (prev.includes(colorHex) ? prev.filter(hex => hex !== colorHex) : [...prev, colorHex]));
  };

  const handleAddToCart = (e: React.MouseEvent, master: MasterSkin, variant: ProductVariant) => {
    e.preventDefault();
    addToCart(master, variant);
    toast({
        title: 'Added to Cart',
        description: `${variant.name} has been added to your cart.`,
    });
  };

  const handleQuantityChange = (e: React.MouseEvent, variantId: number, newQuantity: number) => {
    e.preventDefault();
    updateQuantity(variantId, newQuantity);
  };

  const availableModels = selectedBrands.length > 0 ? models.filter(m => selectedBrands.includes(m.brand_id)) : models;

  const ProductGridSkeleton = () => (
    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
      {Array.from({ length: 6 }).map((_, index) => (
        <Card key={index} className="overflow-hidden flex flex-col bg-card">
          <Skeleton className="w-full h-64" />
          <CardContent className="p-4 flex flex-col flex-grow">
            <Skeleton className="h-6 w-3/4 mb-2" />
            <Skeleton className="h-4 w-1/2 mb-3" />
            <div className="flex-grow"></div>
             <Skeleton className="h-8 w-1/4 mt-auto" />
          </CardContent>
        </Card>
      ))}
    </div>
  );

  return (
    <div className="flex flex-col min-h-screen bg-muted/40">
      <AppHeader />
      <main className="flex-grow container mx-auto px-4 py-8 md:py-12">
        <div className="mb-8 text-center">
            <h1 className="text-3xl md:text-4xl font-headline font-bold">Shop All Skins</h1>
            <p className="text-muted-foreground mt-2">Find the perfect skin for your device.</p>
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          <aside className="lg:col-span-1">
            <div className="sticky top-24 bg-card p-6 rounded-lg shadow-sm">
              <h2 className="text-2xl font-headline font-semibold mb-4">Filters</h2>
              <div className="relative mb-6">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search products..."
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              <Accordion type="multiple" defaultValue={['brands', 'models', 'colors']} className="w-full">
                <AccordionItem value="brands">
                  <AccordionTrigger className="text-lg font-semibold">Brand</AccordionTrigger>
                  <AccordionContent>
                    <div className="space-y-3 p-1">
                      {brands.map((brand: Brand) => (
                        <div key={brand.id} className="flex items-center space-x-2">
                          <Checkbox
                            id={`brand-${brand.id}`}
                            checked={selectedBrands.includes(brand.id)}
                            onCheckedChange={() => handleBrandChange(brand.id)}
                          />
                          <Label htmlFor={`brand-${brand.id}`} className="text-base font-normal cursor-pointer">
                            {brand.name}
                          </Label>
                        </div>
                      ))}
                    </div>
                  </AccordionContent>
                </AccordionItem>
                <AccordionItem value="models">
                  <AccordionTrigger className="text-lg font-semibold">Model</AccordionTrigger>
                  <AccordionContent>
                    <div className="space-y-3 p-1 max-h-60 overflow-y-auto">
                      {availableModels.map((model: PhoneModel) => (
                        <div key={model.id} className="flex items-center space-x-2">
                          <Checkbox
                            id={`model-${model.id}`}
                            checked={selectedModels.includes(model.id)}
                            onCheckedChange={() => handleModelChange(model.id)}
                          />
                          <Label htmlFor={`model-${model.id}`} className="text-base font-normal cursor-pointer">
                            {model.name}
                          </Label>
                        </div>
                      ))}
                    </div>
                  </AccordionContent>
                </AccordionItem>
                <AccordionItem value="colors">
                  <AccordionTrigger className="text-lg font-semibold">Color</AccordionTrigger>
                  <AccordionContent>
                    <div className="space-y-3 p-1">
                      {colorOptions.map(({ hex, name }) => (
                        <div key={hex} className="flex items-center space-x-3">
                          <Checkbox
                            id={`color-${hex}`}
                            checked={selectedColors.includes(hex)}
                            onCheckedChange={() => handleColorChange(hex)}
                          />
                          <div className="w-5 h-5 rounded-full border" style={{ backgroundColor: hex }} />
                          <Label htmlFor={`color-${hex}`} className="text-base font-normal cursor-pointer flex-1">
                            {name}
                          </Label>
                        </div>
                      ))}
                    </div>
                  </AccordionContent>
                </AccordionItem>
              </Accordion>
            </div>
          </aside>
          
          <div className="lg:col-span-3">
            {isLoading ? (
                <ProductGridSkeleton />
            ) : filteredProducts.length > 0 ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
                {filteredProducts.map((product: MasterSkin) => {
                    const primaryVariant = product.variants[0];
                    if (!primaryVariant) return null;
                    const cartItem = cart.find(item => item.id === primaryVariant.id);

                    return (
                        <Link key={product.id} href={`/products/${product.id}`} className="block h-full">
                            <Card className="overflow-hidden group border-2 hover:border-accent transition-all duration-300 transform hover:-translate-y-1 flex flex-col bg-card h-full">
                                <div className="relative">
                                    <Image
                                        src={primaryVariant.image_urls[0]}
                                        alt={primaryVariant.name}
                                        width={400}
                                        height={400}
                                        className="object-cover w-full h-64 transition-transform duration-300 group-hover:scale-105"
                                        data-ai-hint="phone skin"
                                    />
                                </div>
                                <CardContent className="p-4 flex flex-col flex-grow">
                                    <h3 className="text-xl font-headline font-bold">{product.name}</h3>
                                    <p className="text-sm text-muted-foreground mb-2">for {product.model_name}</p>
                                    
                                    <div className="flex-grow"></div>

                                    <div className="flex justify-between items-center mt-auto pt-2">
                                        <p className="text-xl font-bold text-primary">${primaryVariant.price.toFixed(2)}</p>
                                        
                                        {cartItem ? (
                                            <div className="flex items-center gap-1">
                                                <Button variant="outline" size="icon" className="h-9 w-9" onClick={(e) => handleQuantityChange(e, cartItem.id, cartItem.quantity - 1)}>
                                                    <Minus className="h-4 w-4" />
                                                </Button>
                                                <span className="w-8 text-center font-bold text-base">{cartItem.quantity}</span>
                                                <Button variant="outline" size="icon" className="h-9 w-9" onClick={(e) => handleQuantityChange(e, cartItem.id, cartItem.quantity + 1)}>
                                                    <Plus className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        ) : (
                                            <Button onClick={(e) => handleAddToCart(e, product, primaryVariant)}>
                                                <ShoppingCart className="mr-2 h-4 w-4" />
                                                Add
                                            </Button>
                                        )}
                                    </div>
                                </CardContent>
                            </Card>
                        </Link>
                    )
                })}
                </div>
            ) : (
                <div className="flex flex-col items-center justify-center text-center h-full col-span-full py-20 bg-card rounded-lg">
                    <PackageSearch className="w-24 h-24 text-muted-foreground/50 mb-4" />
                    <h2 className="text-2xl font-semibold">No Products Found</h2>
                    <p className="text-muted-foreground mt-2">Try adjusting your filters.</p>
                </div>
            )}
          </div>
        </div>
      </main>
      <AppFooter />
    </div>
  );
}
