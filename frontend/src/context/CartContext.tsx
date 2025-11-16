'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { MasterSkin, ProductVariant } from '@/lib/types';

export interface CartItem extends ProductVariant {
  quantity: number;
  master_name: string;
  model_name: string;
}

interface CartContextType {
  cart: CartItem[];
  addToCart: (masterSkin: Omit<MasterSkin, 'variants'>, variant: ProductVariant, quantity?: number) => void;
  removeFromCart: (variantId: number) => void;
  updateQuantity: (variantId: number, quantity: number) => void;
  clearCart: () => void;
  cartTotal: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [cart, setCart] = useState<CartItem[]>([]);

  useEffect(() => {
    try {
      const savedCart = localStorage.getItem('skinFlexCart');
      if (savedCart) {
        setCart(JSON.parse(savedCart));
      }
    } catch (error) {
      console.error("Could not load cart from localStorage", error);
      setCart([]);
    }
  }, []);

  useEffect(() => {
    try {
      localStorage.setItem('skinFlexCart', JSON.stringify(cart));
    } catch (error) {
      console.error("Could not save cart to localStorage", error);
    }
  }, [cart]);

  const addToCart = (masterSkin: Omit<MasterSkin, 'variants'>, variant: ProductVariant, quantity = 1) => {
    setCart(prevCart => {
      const existingItem = prevCart.find(item => item.id === variant.id);
      if (existingItem) {
        return prevCart.map(item =>
          item.id === variant.id ? { ...item, quantity: item.quantity + quantity } : item
        );
      }
      return [...prevCart, { 
        ...variant, 
        quantity, 
        master_name: masterSkin.name, 
        model_name: masterSkin.model_name 
      }];
    });
  };

  const removeFromCart = (variantId: number) => {
    setCart(prevCart => prevCart.filter(item => item.id !== variantId));
  };

  const updateQuantity = (variantId: number, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(variantId);
    } else {
      setCart(prevCart =>
        prevCart.map(item =>
          item.id === variantId ? { ...item, quantity } : item
        )
      );
    }
  };

  const clearCart = () => {
    setCart([]);
  };

  const cartTotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <CartContext.Provider value={{ cart, addToCart, removeFromCart, updateQuantity, clearCart, cartTotal }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
