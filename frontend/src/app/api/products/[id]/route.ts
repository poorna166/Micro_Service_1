import { NextResponse } from 'next/server';
import { masterSkins, productVariants, models, brands } from '@/lib/mock-data';
import type { MasterSkin } from '@/lib/types';

export async function GET(req: Request, context: {  params: { id: string } }) {
  try {
    const skinId = await parseInt(await context.params.id, 10);

    if (isNaN(skinId)) {
        return NextResponse.json({ error: 'Invalid product ID' }, { status: 400 });
    }
    
    const masterSkin = masterSkins.find(skin => skin.id === skinId);

    if (!masterSkin) {
      return NextResponse.json({ error: 'Product not found' }, { status: 404 });
    }

    const model = models.find(m => m.id === masterSkin.model_id);
    const brand = brands.find(b => b.id === model?.brand_id);
    
    const fullProduct: MasterSkin = {
      ...masterSkin,
      model_name: model?.name || 'Unknown Model',
      variants: productVariants.filter(variant => variant.master_skin_id === masterSkin.id)
    };
    
    (fullProduct as any).brand_name = brand?.name || 'Unknown Brand';


    return NextResponse.json(fullProduct);

  } catch (error) {
    console.error('Failed to fetch product:', error);
    return NextResponse.json({ error: 'Internal Server Error' }, { status: 500 });
  }
}
