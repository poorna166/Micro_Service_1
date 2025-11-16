import { NextResponse } from 'next/server';
import { masterSkins, productVariants, models } from '@/lib/mock-data';
import type { MasterSkin } from '@/lib/types';

export async function GET() {
  // In a real application, you would fetch this data from a database,
  // probably with a JOIN query.
  const skinsWithVariants: MasterSkin[] = masterSkins.map(master => {
    const model = models.find(m => m.id === master.model_id);
    return {
        ...master,
        model_name: model?.name || 'Unknown Model',
        variants: productVariants.filter(variant => variant.master_skin_id === master.id)
    }
  });

  return NextResponse.json(skinsWithVariants);
}
