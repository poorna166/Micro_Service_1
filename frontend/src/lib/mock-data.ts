import type { Brand, PhoneModel, MasterSkin, ProductVariant, Order, CarouselSlide } from './types';

export const brands: Brand[] = [
  { id: 1, name: 'Apple' },
  { id: 2, name: 'Samsung' },
  { id: 3, name: 'Google' },
  { id: 4, name: 'OnePlus' },
];

export const models: PhoneModel[] = [
  { id: 1, brand_id: 1, name: 'iPhone 15 Pro' },
  { id: 2, brand_id: 1, name: 'iPhone 15' },
  { id: 3, brand_id: 2, name: 'Galaxy S24 Ultra' },
  { id: 4, brand_id: 2, name: 'Galaxy Z Fold 5' },
  { id: 5, brand_id: 3, name: 'Pixel 8 Pro' },
  { id: 6, brand_id: 3, name: 'Pixel 8' },
  { id: 7, brand_id: 4, name: 'OnePlus 12' },
  { id: 8, brand_id: 4, name: 'OnePlus Open' },
];

export const masterSkins: Omit<MasterSkin, 'variants' | 'model_name'>[] = [
    { id: 1, model_id: 1, name: 'Carbon Fiber' },
    { id: 2, model_id: 3, name: 'Cosmic' },
    { id: 3, model_id: 5, name: 'Wood Finish' },
    { id: 4, model_id: 7, name: 'Sandstone' },
];

export const productVariants: ProductVariant[] = [
  {
    id: 101,
    master_skin_id: 1,
    name: 'Obsidian Weave',
    price: 24.99,
    image_urls: [
        'https://placehold.co/600x600/000000/FFFFFF.png',
        'https://placehold.co/600x600/111111/FFFFFF.png',
        'https://placehold.co/600x600/222222/FFFFFF.png',
    ],
    color_hex: '#000000',
  },
  {
    id: 102,
    master_skin_id: 1,
    name: 'Crimson Carbon',
    price: 24.99,
    image_urls: [
        'https://placehold.co/600x600/8B0000/FFFFFF.png',
        'https://placehold.co/600x600/9B0000/FFFFFF.png',
        'https://placehold.co/600x600/AB0000/FFFFFF.png',
    ],
    color_hex: '#8B0000',
  },
   {
    id: 103,
    master_skin_id: 1,
    name: 'Cobalt Blue',
    price: 24.99,
    image_urls: [
        'https://placehold.co/600x600/0047AB/FFFFFF.png',
        'https://placehold.co/600x600/0057BB/FFFFFF.png',
        'https://placehold.co/600x600/0067CB/FFFFFF.png',
    ],
    color_hex: '#0047AB',
  },
  {
    id: 201,
    master_skin_id: 2,
    name: 'Cosmic Marble',
    price: 29.99,
    image_urls: [
        'https://placehold.co/600x600/465067/FFFFFF.png',
        'https://placehold.co/600x600/566077/FFFFFF.png',
    ],
    color_hex: '#465067',
  },
  {
    id: 202,
    master_skin_id: 2,
    name: 'Cyber Hex',
    price: 29.99,
    image_urls: [
        'https://placehold.co/600x600/00FFFF/000000.png',
        'https://placehold.co/600x600/11FFFF/000000.png',
        'https://placehold.co/600x600/22FFFF/000000.png',
        'https://placehold.co/600x600/33FFFF/000000.png',
    ],
    color_hex: '#00FFFF',
  },
  {
    id: 301,
    master_skin_id: 3,
    name: 'Matte Black',
    price: 19.99,
    image_urls: ['https://placehold.co/600x600/1C1C1C/FFFFFF.png'],
    color_hex: '#1C1C1C',
  },
  {
    id: 302,
    master_skin_id: 3,
    name: 'Walnut Burl',
    price: 22.99,
    image_urls: [
        'https://placehold.co/600x600/654321/FFFFFF.png',
        'https://placehold.co/600x600/755331/FFFFFF.png',
    ],
    color_hex: '#654321',
  },
  {
    id: 401,
    master_skin_id: 4,
    name: 'Sandstone Red',
    price: 21.99,
    image_urls: ['https://placehold.co/600x600/B22222/FFFFFF.png'],
    color_hex: '#B22222',
  },
  {
    id: 402,
    master_skin_id: 4,
    name: 'Arctic Camo',
    price: 21.99,
    image_urls: ['https://placehold.co/600x600/F5F5F5/000000.png'],
    color_hex: '#F5F5F5',
  },
];

export const orders: Order[] = [
  {
    id: 'SF-001-12345',
    user_id: 1,
    customerName: 'Jane Doe',
    customerPhone: '+1987654321',
    date: '2024-05-28T10:30:00Z',
    total: 54.98,
    paymentStatus: 'Paid',
    shippingStatus: 'Shipped',
    items: [
      { product: { ...productVariants[0], master_name: 'Carbon Fiber', model_name: 'iPhone 15 Pro' }, quantity: 1 },
      { product: { ...productVariants[3], master_name: 'Cosmic', model_name: 'Galaxy S24 Ultra' }, quantity: 1 },
    ],
    shippingAddress: {
      name: 'Jane Doe',
      address: '123 Tech Lane',
      city: 'Silicon Valley',
      state: 'CA',
      zip: '94043',
    },
  },
  {
    id: 'SF-001-12346',
    user_id: 1,
    customerName: 'Jane Doe',
    customerPhone: '+1987654321',
    date: '2024-05-15T14:00:00Z',
    total: 24.99,
    paymentStatus: 'Paid',
    shippingStatus: 'Delivered',
    items: [{ product: { ...productVariants[1], master_name: 'Carbon Fiber', model_name: 'iPhone 15 Pro' }, quantity: 1 }],
    shippingAddress: {
      name: 'Jane Doe',
      address: '123 Tech Lane',
      city: 'Silicon Valley',
      state: 'CA',
      zip: '94043',
    },
  },
    {
    id: 'SF-001-12347',
    user_id: 1,
    customerName: 'Jane Doe',
    customerPhone: '+1987654321',
    date: '2024-06-01T09:00:00Z',
    total: 19.99,
    paymentStatus: 'Paid',
    shippingStatus: 'Processing',
    items: [{ product: { ...productVariants[5], master_name: 'Wood Finish', model_name: 'Pixel 8 Pro'}, quantity: 1 }],
    shippingAddress: {
      name: 'Jane Doe',
      address: '123 Tech Lane',
      city: 'Silicon Valley',
      state: 'CA',
      zip: '94043',
    },
  },
];

// In a real application, this would be stored in a database.
// For this mock setup, it's an in-memory variable.
// Changes will be lost when the server restarts.
export let featuredCollectionIds: number[] = [1, 2, 3, 4];

export const setFeaturedCollectionIds = (ids: number[]) => {
    featuredCollectionIds = ids;
};


export let heroCarouselSlides: CarouselSlide[] = [
    {
        id: 1,
        headline: "Define Your Device",
        description: "Discover exclusive, high-quality skins to give your phone a unique personality.",
        imageUrl: "https://placehold.co/1920x800.png",
        imageHint: "abstract texture",
        ctaText: "Shop All Skins",
        ctaLink: "/products"
    },
    {
        id: 2,
        headline: "New Cosmic Collection!",
        description: "Explore otherworldly designs and give your device a look that's out of this world.",
        imageUrl: "https://placehold.co/1920x800.png",
        imageHint: "cosmic nebula",
        ctaText: "Shop Now",
        ctaLink: "/products"
    },
    {
        id: 3,
        headline: "The SkinFlex Difference",
        description: "Precision fit, premium materials, and durable protection for your device.",
        imageUrl: "https://placehold.co/1920x800.png",
        imageHint: "carbon fiber",
        ctaText: "Learn More",
        ctaLink: "/products"
    }
];

export const setHeroCarouselSlides = (slides: CarouselSlide[]) => {
    heroCarouselSlides = slides;
};
