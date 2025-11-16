import { NextResponse } from 'next/server';
import { productVariants, masterSkins } from '@/lib/mock-data';
import { z } from 'zod';
import type { ProductVariant } from '@/lib/types';

export async function GET() {
  return NextResponse.json(productVariants);
}

const addVariantSchema = z.object({
  name: z.string().min(2, "Variant name must be at least 2 characters"),
  master_skin_id: z.coerce.number().int().positive("A master skin must be selected"),
  price: z.coerce.number().positive("Price must be a positive number"),
  color_hex: z.string().regex(/^#[0-9a-fA-F]{6}$/),
});

export async function POST(req: Request) {
  try {
    const json = await req.json();
    const parsed = addVariantSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name, master_skin_id, price, color_hex } = parsed.data;
    
    const masterSkin = masterSkins.find(m => m.id === master_skin_id);
    if (!masterSkin) {
        return NextResponse.json({ error: 'Invalid master skin ID' }, { status: 400 });
    }

    const newId = Math.max(...productVariants.map(p => p.id), 0) + 1;
    const newVariant: ProductVariant = {
      id: newId,
      master_skin_id,
      name,
      price,
      color_hex,
      image_urls: ['https://placehold.co/600x600/cccccc/FFFFFF.png'],
    };

    return NextResponse.json(newVariant, { status: 201 });

  } catch (error) {
    console.error(error);
    return NextResponse.json({ error: 'Failed to create variant' }, { status: 500 });
  }
}
