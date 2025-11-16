import { NextResponse } from 'next/server';
import { z } from 'zod';
import { featuredCollectionIds, setFeaturedCollectionIds, masterSkins } from '@/lib/mock-data';

export async function GET() {
  return NextResponse.json(featuredCollectionIds);
}

const updateCollectionsSchema = z.object({
  ids: z.array(z.number()),
});

export async function PUT(req: Request) {
  try {
    const json = await req.json();
    const parsed = updateCollectionsSchema.safeParse(json);

    if (!parsed.success) {
      return NextResponse.json({ error: parsed.error.format() }, { status: 400 });
    }

    const { ids } = parsed.data;

    // Validate that all IDs correspond to actual master skins
    const allSkinIds = masterSkins.map(s => s.id);
    const invalidIds = ids.filter(id => !allSkinIds.includes(id));
    if (invalidIds.length > 0) {
        return NextResponse.json({ error: `Invalid master skin IDs: ${invalidIds.join(', ')}` }, { status: 400 });
    }

    setFeaturedCollectionIds(ids); // Update the in-memory array

    return NextResponse.json({ success: true, featuredIds: featuredCollectionIds });

  } catch (error) {
    return NextResponse.json({ error: 'Failed to update featured collections' }, { status: 500 });
  }
}
