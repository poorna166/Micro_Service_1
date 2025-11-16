// import { NextRequest, NextResponse } from 'next/server';
// import Razorpay from 'razorpay';
// import { z } from 'zod';

// const orderSchema = z.object({
//     amount: z.number().positive(),
//     currency: z.string().length(3),
// });

// export async function POST(req: NextRequest) {
//     // This is a placeholder for a real backend implementation.
//     // Do not expose your key secret on the client side.
//     // It should be stored securely as an environment variable on your server.

//     const razorpay = new Razorpay({
//         key_id: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID!,
//         key_secret: process.env.RAZORPAY_KEY_SECRET!,
//     });

//     try {
//         const body = await req.json();
//         const parsedBody = orderSchema.safeParse(body);

//         if (!parsedBody.success) {
//             return NextResponse.json({ error: 'Invalid input' }, { status: 400 });
//         }

//         const options = {
//             amount: parsedBody.data.amount,
//             currency: parsedBody.data.currency,
//             receipt: `receipt_order_${new Date().getTime()}`,
//         };

//         const order = await razorpay.orders.create(options);
        
//         return NextResponse.json(order);

//     } catch (error) {
//         console.error('Error creating Razorpay order:', error);
//         return NextResponse.json({ error: 'Failed to create order' }, { status: 500 });
//     }
// }
