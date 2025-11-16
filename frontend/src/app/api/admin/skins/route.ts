import { NextResponse } from 'next/server';
import { masterSkins, models, productVariants } from '@/lib/mock-data';
import { z } from 'zod';
import type { MasterSkin } from '@/lib/types';

export async function GET() {
  // In a real app, you'd fetch this from your database.
  const skinsWithData = masterSkins.map(skin => ({
      ...skin,
      model_name: models.find(m => m.id === skin.model_id)?.name || 'Unknown',
      variants: productVariants.filter(v => v.master_skin_id === skin.id)
  }));
  return NextResponse.json(skinsWithData);
}

const addSkinSchema = z.object({
  name: z.string().min(2, "Skin name must be at least 2 characters"),
  model_id: z.coerce.number().int().positive("A model must be selected"),
});

// This is a mock implementation.
export async function POST(req: Request) {
  try {
    const json = await req.json();
    const parsed = addSkinSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name, model_id } = parsed.data;
    
    const model = models.find(m => m.id === model_id);
    if (!model) {
        return NextResponse.json({ error: 'Invalid model ID' }, { status: 400 });
    }

    const newId = Math.max(...masterSkins.map(p => p.id), 0) + 1;
    const newSkin: Omit<MasterSkin, 'variants' | 'model_name'> = {
      id: newId,
      model_id,
      name,
    };

    return NextResponse.json(newSkin, { status: 201 });

  } catch (error) {
    console.error(error);
    return NextResponse.json({ error: 'Failed to create master skin' }, { status: 500 });
  }
}
