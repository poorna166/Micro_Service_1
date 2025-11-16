import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Github, Twitter, Instagram } from 'lucide-react';

const AppFooter = () => {
  return (
    <footer className="bg-gray-900 text-gray-300">
      <div className="container mx-auto px-4 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <h3 className="text-2xl font-headline font-bold text-white mb-4">SkinFlex</h3>
            <p className="text-sm">Premium skins for premium devices. Express yourself.</p>
          </div>
          <div>
            <h4 className="font-headline font-semibold text-white mb-4">Shop</h4>
            <ul className="space-y-2 text-sm">
              <li><Link href="/products" className="hover:text-accent">All Skins</Link></li>
              <li><Link href="/products" className="hover:text-accent">Apple</Link></li>
              <li><Link href="/products" className="hover:text-accent">Samsung</Link></li>
              <li><Link href="/products" className="hover:text-accent">Google</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="font-headline font-semibold text-white mb-4">Support</h4>
            <ul className="space-y-2 text-sm">
              <li><Link href="#" className="hover:text-accent">Contact Us</Link></li>
              <li><Link href="#" className="hover:text-accent">FAQ</Link></li>
              <li><Link href="#" className="hover:text-accent">Track Order</Link></li>
              <li><Link href="#" className="hover:text-accent">Shipping & Returns</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="font-headline font-semibold text-white mb-4">Stay Connected</h4>
            <p className="text-sm mb-4">Get updates on new releases and special offers.</p>
            <form className="flex space-x-2">
              <Input type="email" placeholder="Enter your email" className="bg-gray-800 border-gray-700 text-white" />
              <Button type="submit" className="bg-accent text-accent-foreground hover:bg-accent/90">Subscribe</Button>
            </form>
          </div>
        </div>
        <div className="mt-12 pt-8 border-t border-gray-800 flex flex-col sm:flex-row justify-between items-center">
          <p className="text-sm text-gray-500">&copy; {new Date().getFullYear()} SkinFlex. All Rights Reserved.</p>
          <div className="flex space-x-4 mt-4 sm:mt-0">
            <Link href="#" className="text-gray-500 hover:text-accent"><Twitter /></Link>
            <Link href="#" className="text-gray-500 hover:text-accent"><Instagram /></Link>
            <Link href="#" className="text-gray-500 hover:text-accent"><Github /></Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default AppFooter;
