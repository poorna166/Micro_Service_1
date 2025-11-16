export interface Brand {
  id: number;
  name: string;
}

export interface PhoneModel {
  id: number;
  brand_id: number;
  name: string;
}

// MasterSkin represents the core product, e.g., "Marble"
export interface MasterSkin {
  id: number;
  model_id: number;
  model_name: string;
  name: string;
  variants: ProductVariant[];
}

// ProductVariant represents a specific version of a MasterSkin, e.g., "White Marble"
export interface ProductVariant {
  id: number;
  master_skin_id: number;
  name: string;
  price: number;
  image_urls: string[];
  color_hex: string;
}

export interface Order {
  id: string;
  user_id: number;
  customerName: string;
  customerPhone: string;
  date: string;
  total: number;
  paymentStatus: 'Paid' | 'Pending' | 'Failed';
  shippingStatus: 'Processing' | 'Shipped' | 'Delivered' | 'Cancelled';
  items: {
    // The product in an order is a snapshot of the variant at time of purchase
    product: ProductVariant & { master_name: string; model_name: string };
    quantity: number;
  }[];
  shippingAddress: {
    name: string;
    address: string;
    city: string;
    state: string;
    zip: string;
  };
}

export interface CarouselSlide {
  id: number;
  headline: string;
  description: string;
  imageUrl: string;
  ctaText: string;
  ctaLink: string;
  imageHint: string;
}
