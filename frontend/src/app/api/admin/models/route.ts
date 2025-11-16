import { NextResponse } from 'next/server';
import { models, brands } from '@/lib/mock-data';
import { z } from 'zod';
import type { PhoneModel } from '@/lib/types';

export async function GET() {
  // In a real app, you'd fetch this from your database.
  return NextResponse.json(models);
}

const addModelSchema = z.object({
  name: z.string().min(2, "Model name must be at least 2 characters"),
  brand_id: z.coerce.number().int().positive("A brand must be selected"),
});

// This is a mock implementation. In a real app, you'd insert into a database.
export async function POST(req: Request) {
  try {
    const json = await req.json();
    const parsed = addModelSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name, brand_id } = parsed.data;

    // Check if brand exists
    if (!brands.some(b => b.id === brand_id)) {
        return NextResponse.json({ error: 'Invalid brand ID' }, { status: 400 });
    }

    const newId = Math.max(...models.map(m => m.id), 0) + 1;
    const newModel: PhoneModel = {
      id: newId,
      brand_id,
      name,
    };

    return NextResponse.json(newModel, { status: 201 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to create model' }, { status: 500 });
  }
}
