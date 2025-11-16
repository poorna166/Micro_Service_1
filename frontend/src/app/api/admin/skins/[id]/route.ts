import { NextResponse } from 'next/server';
import { z } from 'zod';
import { models } from '@/lib/mock-data';
import type { MasterSkin } from '@/lib/types';

const editSkinSchema = z.object({
  name: z.string().min(2, "Skin name must be at least 2 characters"),
  model_id: z.coerce.number().int().positive("A model must be selected"),
});

export async function PUT(req: Request, { params }: { params: { id: string } }) {
  try {
    const skinId = parseInt(params.id, 10);
    const json = await req.json();
    const parsed = editSkinSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name, model_id } = parsed.data;
    
    const model = models.find(m => m.id === model_id);
    if (!model) {
        return NextResponse.json({ error: 'Invalid model ID' }, { status: 400 });
    }

    const updatedSkin: Omit<MasterSkin, 'variants' | 'model_name'> = {
      id: skinId,
      model_id,
      name,
    };
    
    return NextResponse.json(updatedSkin, { status: 200 });

  } catch (error) {
    console.error(error);
    return NextResponse.json({ error: 'Failed to update master skin' }, { status: 500 });
  }
}

export async function DELETE(req: Request, { params }: { params: { id: string } }) {
  try {
    const skinId = parseInt(params.id, 10);

    console.log(`(Mock) Deleting master skin with ID: ${skinId}`);
    
    return new NextResponse(null, { status: 204 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to delete master skin' }, { status: 500 });
  }
}
