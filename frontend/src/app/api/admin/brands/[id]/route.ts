import { NextResponse } from 'next/server';
import { z } from 'zod';
import type { Brand } from '@/lib/types';

const editBrandSchema = z.object({
  name: z.string().min(2, "Brand name must be at least 2 characters"),
});

// This mock implementation just validates and returns the updated object.
// It doesn't permanently modify the mock-data.ts file.
export async function PUT(req: Request, { params }: { params: { id: string } }) {
  try {
    const brandId = parseInt(params.id, 10);
    const json = await req.json();
    const parsed = editBrandSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }
    
    const updatedBrand: Brand = {
      id: brandId,
      name: parsed.data.name,
    };
    
    // In a real app with a database, you would perform an UPDATE query here.
    
    return NextResponse.json(updatedBrand, { status: 200 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to update brand' }, { status: 500 });
  }
}

// This mock implementation just returns success.
// The client-side state is responsible for removing the item from the view.
export async function DELETE(req: Request, { params }: { params: { id: string } }) {
  try {
    const brandId = parseInt(params.id, 10);

    // In a real app with a database, you would perform a DELETE query here.
    console.log(`(Mock) Deleting brand with ID: ${brandId}`);
    
    return new NextResponse(null, { status: 204 }); // 204 No Content is standard for successful DELETE

  } catch (error) {
    return NextResponse.json({ error: 'Failed to delete brand' }, { status: 500 });
  }
}
