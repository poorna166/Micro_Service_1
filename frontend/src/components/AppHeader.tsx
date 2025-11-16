
'use client';

import Link from 'next/link';
import { ShoppingCart, User, Menu, LogOut, Package, Shield, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useCart } from '@/context/CartContext';
import { useAuth } from '@/context/AuthContext';
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetClose
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
  DropdownMenuLabel,
} from "@/components/ui/dropdown-menu";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu"
import { brands, models } from '@/lib/mock-data';
import { cn } from '@/lib/utils';
import React from 'react';


const AppHeader = () => {
  const { cart } = useCart();
  const { user, isAdmin, loading, logout } = useAuth();
  const cartItemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <header className="sticky top-0 z-50 bg-background/80 backdrop-blur-sm border-b">
      <div className="container mx-auto px-4 flex justify-between items-center h-20">
        <Link href="/" className="text-3xl font-headline font-bold text-primary mr-8">
          SkinFlex
        </Link>
        
        {/* Desktop Navigation */}
        <div className="flex-1 hidden md:flex justify-start">
          <NavigationMenu>
            <NavigationMenuList>
              <NavigationMenuItem>
                <NavigationMenuLink asChild>
                  <Link href="/" className={navigationMenuTriggerStyle()}>
                    Home
                  </Link>
                </NavigationMenuLink>
              </NavigationMenuItem>
              <NavigationMenuItem>
                <NavigationMenuTrigger>Shop by Brand</NavigationMenuTrigger>
                <NavigationMenuContent>
                  <ul className="grid w-[400px] gap-3 p-4 md:w-[500px] md:grid-cols-2 lg:w-[600px] ">
                    {brands.map((brand) => (
                      <ListItem key={brand.name} title={brand.name} href="/products">
                        {models.filter(m => m.brand_id === brand.id).map(m => m.name).join(', ')}
                      </ListItem>
                    ))}
                  </ul>
                </NavigationMenuContent>
              </NavigationMenuItem>
               <NavigationMenuItem>
                  <NavigationMenuLink asChild>
                    <Link href="/products" className={navigationMenuTriggerStyle()}>
                      All Skins
                    </Link>
                  </NavigationMenuLink>
              </NavigationMenuItem>
              {isAdmin && (
                 <NavigationMenuItem>
                    <NavigationMenuLink asChild>
                      <Link href="/admin" className={navigationMenuTriggerStyle()}>
                          Admin
                      </Link>
                    </NavigationMenuLink>
                </NavigationMenuItem>
              )}
            </NavigationMenuList>
          </NavigationMenu>
        </div>
        
        {/* Right side icons */}
        <div className="flex items-center space-x-2 md:space-x-4">
          <Button asChild variant="ghost" size="icon">
              <Link href="/products">
                <Search className="h-6 w-6" />
                <span className="sr-only">Search</span>
              </Link>
            </Button>
            
          {user ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <User className="h-6 w-6" />
                  <span className="sr-only">User Account</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuLabel>My Account</DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link href="/account/orders">
                    <Package className="mr-2 h-4 w-4" />
                    <span>My Orders</span>
                  </Link>
                </DropdownMenuItem>
                 {isAdmin && (
                  <DropdownMenuItem asChild>
                    <Link href="/admin">
                      <Shield className="mr-2 h-4 w-4" />
                      <span>Admin Panel</span>
                    </Link>
                  </DropdownMenuItem>
                )}
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={logout}>
                  <LogOut className="mr-2 h-4 w-4" />
                  <span>Logout</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : !loading && (
            <Button asChild variant="ghost" size="icon">
              <Link href="/login">
                <User className="h-6 w-6" />
                <span className="sr-only">Login</span>
              </Link>
            </Button>
          )}

          <Button asChild variant="ghost" size="icon" className="relative">
            <Link href="/cart">
              <ShoppingCart className="h-6 w-6" />
              {cartItemCount > 0 && (
                <span className="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-accent text-accent-foreground text-xs">
                  {cartItemCount}
                </span>
              )}
              <span className="sr-only">Cart</span>
            </Link>
          </Button>
          
          {/* Mobile Navigation */}
          <div className="md:hidden">
            <Sheet>
              <SheetTrigger asChild>
                <Button variant="ghost" size="icon">
                  <Menu className="h-6 w-6" />
                </Button>
              </SheetTrigger>
              <SheetContent side="right" className="p-0">
                <div className="flex flex-col h-full">
                    <div className="p-4 border-b">
                        <SheetClose asChild>
                            <Link href="/" className="text-2xl font-headline font-bold text-primary">
                                SkinFlex
                            </Link>
                        </SheetClose>
                    </div>
                    <div className="flex-1 overflow-y-auto p-4 space-y-2">
                        <Accordion type="multiple" className="w-full">
                            <AccordionItem value="shop">
                                <AccordionTrigger className="text-lg font-medium hover:no-underline">Shop by Brand</AccordionTrigger>
                                <AccordionContent className="pl-2">
                                    <Accordion type="multiple" className="w-full">
                                        {brands.map((brand) => (
                                            <AccordionItem key={brand.id} value={`brand-${brand.id}`} className="border-b-0">
                                                <AccordionTrigger className="text-base font-normal hover:no-underline">{brand.name}</AccordionTrigger>
                                                <AccordionContent className="pl-4 pb-0">
                                                    {models.filter(m => m.brand_id === brand.id).map(model => (
                                                        <SheetClose asChild key={model.id}>
                                                            <Link href="/products" className="block py-2 text-muted-foreground hover:text-foreground">
                                                                {model.name}
                                                            </Link>
                                                        </SheetClose>
                                                    ))}
                                                </AccordionContent>
                                            </AccordionItem>
                                        ))}
                                    </Accordion>
                                </AccordionContent>
                            </AccordionItem>
                        </Accordion>
                        <SheetClose asChild>
                            <Link href="/products" className="block py-2 text-lg font-medium">
                                All Skins
                            </Link>
                        </SheetClose>
                    </div>
                    <div className="mt-auto p-4 border-t">
                        {loading ? (
                          <div className="h-10"></div>
                        ) : user ? (
                            <div className="space-y-4">
                                <SheetClose asChild>
                                    <Link href="/account/orders" className="flex items-center w-full text-lg font-medium">
                                        <Package className="mr-2 h-5 w-5" /> My Orders
                                    </Link>
                                </SheetClose>
                                {isAdmin && (
                                     <SheetClose asChild>
                                        <Link href="/admin" className="flex items-center w-full text-lg font-medium">
                                            <Shield className="mr-2 h-5 w-5" /> Admin Panel
                                        </Link>
                                     </SheetClose>
                                )}
                                <SheetClose asChild>
                                    <Button variant="ghost" onClick={logout} className="w-full justify-start p-0 text-lg font-medium h-auto">
                                        <LogOut className="mr-2 h-5 w-5" /> Logout
                                    </Button>
                                </SheetClose>
                            </div>
                        ) : (
                            <SheetClose asChild>
                                <Link href="/login" className="flex items-center w-full text-lg font-medium">
                                    <User className="mr-2 h-5 w-5" /> Login / Sign Up
                                </Link>
                            </SheetClose>
                        )}
                    </div>
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </div>
    </header>
  );
};


const ListItem = React.forwardRef<
  React.ElementRef<"a">,
  React.ComponentPropsWithoutRef<"a">
>(({ className, title, children, ...props }, ref) => {
  return (
    <li>
      <NavigationMenuLink asChild>
        <Link
          ref={ref}
          className={cn(
            "block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground",
            className
          )}
          {...props}
        >
          <div className="text-sm font-medium leading-none">{title}</div>
          <p className="line-clamp-2 text-sm leading-snug text-muted-foreground">
            {children}
          </p>
        </Link>
      </NavigationMenuLink>
    </li>
  )
})
ListItem.displayName = "ListItem"


export default AppHeader;
