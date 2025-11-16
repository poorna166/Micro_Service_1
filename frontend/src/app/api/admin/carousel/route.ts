import { NextResponse } from 'next/server';
import { heroCarouselSlides } from '@/lib/mock-data';
import { z } from 'zod';
import type { CarouselSlide } from '@/lib/types';

export async function GET() {
  return NextResponse.json(heroCarouselSlides);
}

const addSlideSchema = z.object({
    headline: z.string().min(1),
    description: z.string().min(1),
    imageUrl: z.string().url(),
    imageHint: z.string(),
    ctaText: z.string().min(1),
    ctaLink: z.string().min(1),
});

export async function POST(req: Request) {
  try {
    const json = await req.json();
    const parsed = addSlideSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const newId = Math.max(...heroCarouselSlides.map(s => s.id), 0) + 1;
    const newSlide: CarouselSlide = {
      id: newId,
      ...parsed.data,
    };

    return NextResponse.json(newSlide, { status: 201 });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to create slide' }, { status: 500 });
  }
}
