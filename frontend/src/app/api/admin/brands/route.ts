import { NextResponse } from 'next/server';
import { brands } from '@/lib/mock-data';
import { z } from 'zod';
import type { Brand } from '@/lib/types';

export async function GET() {
  // In a real app, you'd fetch this from your database.
  // For now, we're serving from the mock data file.
  return NextResponse.json(brands);
}

const addBrandSchema = z.object({
  name: z.string().min(2, "Brand name must be at least 2 characters"),
});

// This is a mock implementation. In a real app, you'd insert into a database.
// The 'brands' array is in-memory and will reset on server restart.
export async function POST(req: Request) {
  try {
    const json = await req.json();
    const parsed = addBrandSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { name } = parsed.data;

    // Simulate creating a new brand by finding the highest current ID and adding 1
    const newId = Math.max(...brands.map(b => b.id), 0) + 1;
    const newBrand: Brand = {
      id: newId,
      name,
    };
    
    // In a real app, you would add this to your database.
    // For this mock, we just return the new object. The client will update its state.
    // brands.push(newBrand); // This won't persist across requests on the server.

    return NextResponse.json(newBrand, { status: 201 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to create brand' }, { status: 500 });
  }
}
