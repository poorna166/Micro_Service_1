import { NextResponse } from 'next/server';
import { z } from 'zod';
import { masterSkins } from '@/lib/mock-data';
import type { ProductVariant } from '@/lib/types';

const editVariantSchema = z.object({
  name: z.string().min(2, "Variant name must be at least 2 characters"),
  master_skin_id: z.coerce.number().int().positive("A master skin must be selected"),
  price: z.coerce.number().positive("Price must be a positive number"),
  color_hex: z.string().regex(/^#[0-9a-fA-F]{6}$/),
});

export async function PUT(req: Request, { params }: { params: { id: string } }) {
  try {
    const variantId = parseInt(params.id, 10);
    const json = await req.json();
    const parsed = editVariantSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name, master_skin_id, price, color_hex } = parsed.data;
    
    if (!masterSkins.some(m => m.id === master_skin_id)) {
        return NextResponse.json({ error: 'Invalid master skin ID' }, { status: 400 });
    }

    const updatedVariant: ProductVariant = {
      id: variantId,
      master_skin_id,
      name,
      price,
      color_hex,
      image_urls: ['https://placehold.co/600x600/cccccc/FFFFFF.png'],
    };
    
    return NextResponse.json(updatedVariant, { status: 200 });

  } catch (error) {
    console.error(error);
    return NextResponse.json({ error: 'Failed to update variant' }, { status: 500 });
  }
}

export async function DELETE(req: Request, { params }: { params: { id: string } }) {
  try {
    const variantId = parseInt(params.id, 10);
    console.log(`(Mock) Deleting variant with ID: ${variantId}`);
    return new NextResponse(null, { status: 204 });
  } catch (error) {
    return NextResponse.json({ error: 'Failed to delete variant' }, { status: 500 });
  }
}
