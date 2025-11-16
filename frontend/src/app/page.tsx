'use client';

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { Skeleton } from '@/components/ui/skeleton';
import { ArrowRight, ShieldCheck, Gem, Layers } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';
import AppHeader from '@/components/AppHeader';
import AppFooter from '@/components/AppFooter';
import type { MasterSkin, CarouselSlide } from '@/lib/types';

export default function HomePage() {
  const [featuredProducts, setFeaturedProducts] = useState<MasterSkin[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [heroSlides, setHeroSlides] = useState<CarouselSlide[]>([]);
  const [isHeroLoading, setIsHeroLoading] = useState(true);

  useEffect(() => {
    const fetchFeaturedProducts = async () => {
        setIsLoading(true);
        try {
            const res = await fetch('/api/products/featured');
            const data = await res.json();
            setFeaturedProducts(data);
        } catch (error) {
            console.error('Failed to fetch featured products:', error);
        } finally {
            setIsLoading(false);
        }
    };
    const fetchHeroSlides = async () => {
        setIsHeroLoading(true);
        try {
            const res = await fetch('/api/carousel');
            if (!res.ok) throw new Error('Failed to fetch slides');
            const data = await res.json();
            setHeroSlides(data);
        } catch (error) {
            console.error('Failed to fetch hero slides:', error);
        } finally {
            setIsHeroLoading(false);
        }
    };
    
    fetchFeaturedProducts();
    fetchHeroSlides();
  }, []);

  const FeaturedProductSkeleton = () => (
    <CarouselItem className="basis-full sm:basis-1/2 lg:basis-1/3 xl:basis-1/4">
      <div className="p-1">
        <Card className="overflow-hidden h-full flex flex-col">
            <Skeleton className="h-64 w-full" />
            <div className="p-6 flex flex-col flex-grow">
                <Skeleton className="h-6 w-3/4 mb-2" />
                <Skeleton className="h-4 w-1/2" />
            </div>
        </Card>
      </div>
    </CarouselItem>
  );

  const HeroCarouselSkeleton = () => (
    <div className="relative h-[60vh] md:h-[70vh] w-full">
        <Skeleton className="w-full h-full" />
        <div className="absolute inset-0 flex flex-col items-center justify-center text-center text-white p-4 bg-black/30">
            <Skeleton className="h-12 md:h-16 w-3/4 mb-4" />
            <Skeleton className="h-6 md:h-7 w-1/2 mb-8" />
            <Skeleton className="h-12 w-48" />
        </div>
    </div>
  );

  return (
    <div className="flex flex-col min-h-screen">
      <AppHeader />

      <main className="flex-grow">
        {/* Hero Carousel Section */}
        <section className="relative w-full">
            <Carousel opts={{ loop: true }} className="w-full">
                <CarouselContent>
                    {isHeroLoading ? (
                        <CarouselItem>
                           <HeroCarouselSkeleton />
                        </CarouselItem>
                    ) : (
                        heroSlides.map((slide) => (
                            <CarouselItem key={slide.id}>
                                <div className="relative h-[60vh] md:h-[70vh] w-full">
                                    <Image
                                        src={slide.imageUrl}
                                        alt={slide.headline}
                                        fill
                                        className="object-cover brightness-50"
                                        data-ai-hint={slide.imageHint}
                                        priority={slide.id === 1}
                                    />
                                    <div className="absolute inset-0 flex flex-col items-center justify-center text-center text-white p-4">
                                        <h1 className="text-4xl md:text-6xl font-headline font-bold mb-4 drop-shadow-lg">
                                            {slide.headline}
                                        </h1>
                                        <p className="text-lg md:text-xl max-w-2xl mx-auto mb-8 drop-shadow-md">
                                            {slide.description}
                                        </p>
                                        <Button asChild size="lg" className="bg-white text-primary hover:bg-gray-200">
                                            <Link href={slide.ctaLink}>
                                                {slide.ctaText} <ArrowRight className="ml-2 h-5 w-5" />
                                            </Link>
                                        </Button>
                                    </div>
                                </div>
                            </CarouselItem>
                        ))
                    )}
                </CarouselContent>
                <CarouselPrevious className="absolute left-4 top-1/2 -translate-y-1/2 z-10 text-white bg-black/30 hover:bg-black/50 border-none h-10 w-10 md:flex hidden" />
                <CarouselNext className="absolute right-4 top-1/2 -translate-y-1/2 z-10 text-white bg-black/30 hover:bg-black/50 border-none h-10 w-10 md:flex hidden" />
            </Carousel>
        </section>

        {/* Featured Products Section */}
        <section className="py-16 md:py-24">
          <div className="container mx-auto px-4">
            <h2 className="text-3xl md:text-4xl font-headline font-bold text-center mb-12">
              Featured Collections
            </h2>
            <Carousel
              opts={{
                align: "start",
                loop: true,
              }}
              className="w-full"
            >
              <CarouselContent>
                {isLoading ? (
                    Array.from({ length: 4 }).map((_, index) => <FeaturedProductSkeleton key={index} />)
                ) : (
                    featuredProducts.map((product) => (
                    <CarouselItem key={product.id} className="basis-full sm:basis-1/2 lg:basis-1/3 xl:basis-1/4">
                        <div className="p-1 h-full">
                        <Link href={`/products/${product.id}`} className="block h-full">
                            <Card className="overflow-hidden group border-2 hover:border-accent transition-all duration-300 transform hover:-translate-y-2 h-full flex flex-col">
                            <CardContent className="p-0 flex-grow flex flex-col">
                                <Image
                                src={product.variants[0]?.image_urls[0] || 'https://placehold.co/400x400.png'}
                                alt={product.name}
                                width={400}
                                height={400}
                                className="object-cover w-full h-64 transition-transform duration-300 group-hover:scale-105"
                                data-ai-hint="phone skin"
                                />
                                <div className="p-6 flex flex-col flex-grow">
                                <h3 className="text-xl font-headline font-bold mb-2">{product.name}</h3>
                                <p className="text-muted-foreground flex-grow">for {product.model_name}</p>
                                </div>
                            </CardContent>
                            </Card>
                        </Link>
                        </div>
                    </CarouselItem>
                    ))
                )}
              </CarouselContent>
              <CarouselPrevious className="hidden md:flex" />
              <CarouselNext className="hidden md:flex" />
            </Carousel>
          </div>
        </section>

        {/* Quality Promise Section */}
        <section className="bg-muted py-16 md:py-24">
          <div className="container mx-auto px-4">
            <h2 className="text-3xl md:text-4xl font-headline font-bold text-center mb-12">
              The SkinFlex Difference
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-12 text-center">
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center w-20 h-20 rounded-full bg-primary text-primary-foreground mb-6">
                  <Layers className="w-10 h-10" />
                </div>
                <h3 className="text-2xl font-headline font-bold mb-3">Premium Materials</h3>
                <p className="text-muted-foreground">
                  We use only the highest-grade 3M vinyl for a perfect fit and bubble-free application.
                </p>
              </div>
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center w-20 h-20 rounded-full bg-primary text-primary-foreground mb-6">
                  <Gem className="w-10 h-10" />
                </div>
                <h3 className="text-2xl font-headline font-bold mb-3">Exquisite Designs</h3>
                <p className="text-muted-foreground">
                  Our skins feature unique, high-resolution prints you won't find anywhere else.
                </p>
              </div>
              <div className="flex flex-col items-center">
                <div className="flex items-center justify-center w-20 h-20 rounded-full bg-primary text-primary-foreground mb-6">
                  <ShieldCheck className="w-10 h-10" />
                </div>
                <h3 className="text-2xl font-headline font-bold mb-3">Durable Protection</h3>
                <p className="text-muted-foreground">
                  Protect your device from everyday scratches, scuffs, and minor drops.
                </p>
              </div>
            </div>
          </div>
        </section>
      </main>

      <AppFooter />
    </div>
  );
}
