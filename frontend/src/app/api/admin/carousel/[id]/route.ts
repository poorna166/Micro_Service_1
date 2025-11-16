import { NextResponse } from 'next/server';
import { z } from 'zod';
import type { CarouselSlide } from '@/lib/types';

const editSlideSchema = z.object({
    headline: z.string().min(1),
    description: z.string().min(1),
    imageUrl: z.string().url(),
    imageHint: z.string(),
    ctaText: z.string().min(1),
    ctaLink: z.string().min(1),
});

export async function PUT(req: Request, { params }: { params: { id: string } }) {
  try {
    const slideId = parseInt(params.id, 10);
    const json = await req.json();
    const parsed = editSlideSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }
    
    const updatedSlide: CarouselSlide = {
      id: slideId,
      ...parsed.data,
    };
    
    return NextResponse.json(updatedSlide, { status: 200 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to update slide' }, { status: 500 });
  }
}

export async function DELETE(req: Request, { params }: { params: { id: string } }) {
  try {
    const slideId = parseInt(params.id, 10);
    console.log(`(Mock) Deleting carousel slide with ID: ${slideId}`);
    return new NextResponse(null, { status: 204 });
  } catch (error) {
    return NextResponse.json({ error: 'Failed to delete slide' }, { status: 500 });
  }
}
