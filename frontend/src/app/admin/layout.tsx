'use client'

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { LayoutDashboard, ShoppingBag, Smartphone, Tag, ListOrdered, ChevronLeft, ChevronRight, Loader2, Palette, Shapes, GalleryHorizontal } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useState, useEffect } from 'react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/context/AuthContext';

const AdminLayout = ({ children }: { children: React.ReactNode }) => {
    const pathname = usePathname();
    const router = useRouter();
    const { user, isAdmin, loading } = useAuth();
    const [isCollapsed, setIsCollapsed] = useState(false);

    useEffect(() => {
        if (!loading && !isAdmin) {
            router.push('/');
        }
    }, [user, isAdmin, loading, router]);

    const navItems = [
        { href: '/admin', label: 'Dashboard', icon: LayoutDashboard },
        { href: '/admin/brands', label: 'Brands', icon: Tag },
        { href: '/admin/models', label: 'Models', icon: Smartphone },
        { href: '/admin/skins', label: 'Master Skins', icon: ShoppingBag },
        { href: '/admin/variants', label: 'Variants', icon: Palette },
        { href: '/admin/collections', label: 'Featured', icon: Shapes },
        { href: '/admin/carousel', label: 'Hero Carousel', icon: GalleryHorizontal },
        { href: '/admin/orders', label: 'Orders', icon: ListOrdered },
    ];

    if (loading || !isAdmin) {
        return (
            <div className="flex h-screen w-full items-center justify-center">
                <Loader2 className="h-8 w-8 animate-spin" />
            </div>
        );
    }

    return (
        <div className="flex min-h-screen bg-muted/40">
            <aside className={cn(
                "flex flex-col text-white bg-gray-900 transition-all duration-300 ease-in-out",
                isCollapsed ? "w-20" : "w-64"
            )}>
                <div className={cn("flex items-center h-20 border-b border-gray-800 px-6", isCollapsed ? "justify-center" : "justify-between")}>
                    {!isCollapsed && <Link href="/"><span className="text-2xl font-bold font-headline">SkinFlex</span></Link>}
                     <Button variant="ghost" size="icon" onClick={() => setIsCollapsed(!isCollapsed)} className="text-white hover:bg-gray-700 hover:text-white">
                        {isCollapsed ? <ChevronRight /> : <ChevronLeft />}
                    </Button>
                </div>
                <nav className="flex-1 px-4 py-6 space-y-2">
                    {navItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = pathname === item.href;
                        return (
                            <Link
                                key={item.href}
                                href={item.href}
                                className={cn(
                                    "flex items-center gap-4 rounded-lg px-4 py-2 transition-colors",
                                    isActive
                                        ? "bg-accent text-accent-foreground"
                                        : "hover:bg-gray-700",
                                    isCollapsed && "justify-center"
                                )}
                            >
                                <Icon className="h-5 w-5" />
                                {!isCollapsed && <span>{item.label}</span>}
                            </Link>
                        );
                    })}
                </nav>
            </aside>
            <div className="flex-1 flex flex-col">
                 <header className="flex h-20 items-center justify-between border-b bg-white px-6">
                    <h1 className="text-xl font-semibold font-headline">
                        {navItems.find(item => item.href === pathname)?.label || 'Admin'}
                    </h1>
                     <Link href="/">
                        <Button variant="outline">Back to Store</Button>
                    </Link>
                </header>
                <main className="flex-1 p-6 overflow-auto">
                    {children}
                </main>
            </div>
        </div>
    );
};

export default AdminLayout;
