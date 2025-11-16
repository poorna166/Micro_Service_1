import { NextResponse } from 'next/server';
import { featuredCollectionIds, masterSkins, models, productVariants } from '@/lib/mock-data';
import type { MasterSkin } from '@/lib/types';

export const dynamic = 'force-dynamic';

export async function GET() {
  try {
    const featuredSkins = masterSkins
      .filter(skin => featuredCollectionIds.includes(skin.id))
      .map(skin => {
        const model = models.find(m => m.id === skin.model_id);
        const variants = productVariants.filter(v => v.master_skin_id === skin.id);
        return {
          ...skin,
          model_name: model?.name || 'Unknown Model',
          variants: variants,
        };
      });

    // To maintain the order set by the admin
    const orderedFeaturedSkins = featuredCollectionIds
        .map(id => featuredSkins.find(skin => skin.id === id))
        .filter((skin): skin is MasterSkin => skin !== undefined);

    return NextResponse.json(orderedFeaturedSkins);
  } catch (error) {
    console.error('Failed to fetch featured products:', error);
    return NextResponse.json({ error: 'Internal Server Error' }, { status: 500 });
  }
}
