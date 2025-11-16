import { NextResponse } from 'next/server';
import { z } from 'zod';
import { models } from '@/lib/mock-data';
import type { PhoneModel } from '@/lib/types';

const editModelSchema = z.object({
  name: z.string().min(2, "Model name must be at least 2 characters"),
  brand_id: z.coerce.number().int().positive("A brand must be selected"),
});

export async function PUT(req: Request, { params }: { params: { id: string } }) {
  try {
    const modelId = parseInt(params.id, 10);
    const json = await req.json();
    const parsed = editModelSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }
    
    const updatedModel: PhoneModel = {
      id: modelId,
      ...parsed.data,
    };
    
    return NextResponse.json(updatedModel, { status: 200 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to update model' }, { status: 500 });
  }
}

export async function DELETE(req: Request, { params }: { params: { id: string } }) {
  try {
    const modelId = parseInt(params.id, 10);
    
    console.log(`(Mock) Deleting model with ID: ${modelId}`);
    
    return new NextResponse(null, { status: 204 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to delete model' }, { status: 500 });
  }
}
