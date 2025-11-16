import { NextResponse } from 'next/server';
import { orders } from '@/lib/mock-data';

export async function GET() {
  // In a real app, you'd fetch this from your database.
  return NextResponse.json(orders);
}
