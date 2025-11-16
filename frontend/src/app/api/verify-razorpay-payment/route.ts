// import { NextRequest, NextResponse } from 'next/server';
// import crypto from 'crypto';
// import { z } from 'zod';

// const verificationSchema = z.object({
//   razorpay_order_id: z.string(),
//   razorpay_payment_id: z.string(),
//   razorpay_signature: z.string(),
// });

// export async function POST(req: NextRequest) {
//     // This is a placeholder for a real backend implementation.
//     // The key secret should only be on the server.
//     const secret = process.env.RAZORPAY_KEY_SECRET!;

//     try {
//         const body = await req.json();
//         const parsedBody = verificationSchema.safeParse(body);

//         if (!parsedBody.success) {
//             return NextResponse.json({ error: 'Invalid input' }, { status: 400 });
//         }

//         const { razorpay_order_id, razorpay_payment_id, razorpay_signature } = parsedBody.data;

//         const shasum = crypto.createHmac('sha256', secret);
//         shasum.update(`${razorpay_order_id}|${razorpay_payment_id}`);
//         const digest = shasum.digest('hex');

//         if (digest === razorpay_signature) {
//             // Payment is legitimate.
//             // Here you would typically update your database with the payment details
//             // and mark the order as paid.
//             console.log('Payment verified successfully for order:', razorpay_order_id);
//             return NextResponse.json({ status: 'ok' });
//         } else {
//             return NextResponse.json({ error: 'Invalid signature' }, { status: 400 });
//         }
//     } catch (error) {
//         console.error('Error verifying Razorpay payment:', error);
//         return NextResponse.json({ error: 'Payment verification failed' }, { status: 500 });
//     }
// }
