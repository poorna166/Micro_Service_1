'use client';

import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Loader2, Save } from 'lucide-react';
import type { MasterSkin } from '@/lib/types';
import { useToast } from '@/hooks/use-toast';
import Image from 'next/image';
import { Switch } from '@/components/ui/switch';

export default function AdminCollectionsPage() {
    const [allSkins, setAllSkins] = useState<MasterSkin[]>([]);
    const [featuredIds, setFeaturedIds] = useState<Set<number>>(new Set());
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const { toast } = useToast();

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const [skinsRes, collectionsRes] = await Promise.all([
                    fetch('/api/admin/skins'),
                    fetch('/api/admin/collections')
                ]);
                const skinsData = await skinsRes.json();
                const collectionsData: number[] = await collectionsRes.json();
                setAllSkins(skinsData);
                setFeaturedIds(new Set(collectionsData));
            } catch (error) {
                console.error("Failed to fetch data", error);
                toast({ variant: "destructive", title: "Error", description: "Failed to fetch page data." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [toast]);

    const handleToggleFeatured = (skinId: number) => {
        setFeaturedIds(prev => {
            const newSet = new Set(prev);
            if (newSet.has(skinId)) {
                newSet.delete(skinId);
            } else {
                newSet.add(skinId);
            }
            return newSet;
        });
    };

    const handleSaveChanges = async () => {
        setIsSubmitting(true);
        try {
            const response = await fetch('/api/admin/collections', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ids: Array.from(featuredIds) }),
            });

            if (!response.ok) {
                throw new Error('Failed to save changes');
            }

            toast({ title: "Success", description: "Featured collections updated successfully." });
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to save changes." });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="space-y-6">
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <div>
                        <CardTitle>Manage Featured Collections</CardTitle>
                        <CardDescription>
                            Toggle which skins appear in the "Featured Collections" section on the homepage.
                        </CardDescription>
                    </div>
                    <Button onClick={handleSaveChanges} disabled={isSubmitting || isLoading}>
                        {isSubmitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                        Save Changes
                    </Button>
                </CardHeader>
                <CardContent>
                    {isLoading ? (
                        <div className="flex justify-center items-center py-8">
                           <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                       </div>
                    ) : (
                         <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Skin</TableHead>
                                    <TableHead className="text-right">Featured</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {allSkins.map((skin) => (
                                    <TableRow key={skin.id}>
                                        <TableCell>
                                            <div className="flex items-center gap-3">
                                                <Image src={skin.variants[0]?.image_urls[0] || 'https://placehold.co/40x40.png'} alt={skin.name} width={40} height={40} className="rounded-md border" data-ai-hint="phone skin" />
                                                <div>
                                                    <div className="font-medium">{skin.name}</div>
                                                    <div className="text-sm text-muted-foreground">{skin.model_name}</div>
                                                </div>
                                            </div>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex items-center justify-end">
                                                <Switch
                                                    id={`featured-switch-${skin.id}`}
                                                    checked={featuredIds.has(skin.id)}
                                                    onCheckedChange={() => handleToggleFeatured(skin.id)}
                                                    aria-label={`Toggle featured status for ${skin.name}`}
                                                />
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
