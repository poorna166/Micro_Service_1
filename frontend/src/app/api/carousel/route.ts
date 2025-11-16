import { NextResponse } from 'next/server';
import { heroCarouselSlides } from '@/lib/mock-data';

export const dynamic = 'force-dynamic';

export async function GET() {
  return NextResponse.json(heroCarouselSlides);
}
