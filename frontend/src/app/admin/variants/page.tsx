'use client';

import { useState, useEffect } from "react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { PlusCircle, MoreHorizontal, Loader2, Edit, Trash2 } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import type { ProductVariant, MasterSkin } from "@/lib/types";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";

const variantFormSchema = z.object({
  master_skin_id: z.string({ required_error: "Please select a master skin." }),
  name: z.string().min(2, "Variant name must be at least 2 characters."),
  price: z.coerce.number().positive("Price must be a positive number."),
  color_hex: z.string().regex(/^#[0-9a-fA-F]{6}$/, { message: "Must be a valid hex color code (e.g. #RRGGBB)" }),
});

export default function AdminVariantsPage() {
    const [variants, setVariants] = useState<ProductVariant[]>([]);
    const [masterSkins, setMasterSkins] = useState<MasterSkin[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingVariant, setEditingVariant] = useState<ProductVariant | null>(null);
    const [variantToDelete, setVariantToDelete] = useState<ProductVariant | null>(null);
    const { toast } = useToast();

    const form = useForm<z.infer<typeof variantFormSchema>>({
        resolver: zodResolver(variantFormSchema),
        defaultValues: { name: "", color_hex: "#" },
    });

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const [variantsRes, masterSkinsRes] = await Promise.all([
                    fetch('/api/admin/variants'),
                    fetch('/api/admin/skins')
                ]);
                const variantsData = await variantsRes.json();
                const masterSkinsData = await masterSkinsRes.json();
                setVariants(variantsData);
                setMasterSkins(masterSkinsData);
            } catch (error) {
                console.error("Failed to fetch data", error);
                toast({ variant: "destructive", title: "Error", description: "Failed to fetch data." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [toast]);

    const getMasterSkinName = (masterSkinId: number) => {
        return masterSkins.find(s => s.id === masterSkinId)?.name || 'Unknown';
    }

    const handleDialogOpen = (variant: ProductVariant | null = null) => {
        if (variant) {
            setEditingVariant(variant);
            form.reset({
                master_skin_id: String(variant.master_skin_id),
                name: variant.name,
                price: variant.price,
                color_hex: variant.color_hex,
            });
        } else {
            setEditingVariant(null);
            form.reset({ name: "", price: undefined, master_skin_id: undefined, color_hex: "#" });
        }
        setIsDialogOpen(true);
    }
    
    const handleDialogClose = () => {
        setIsDialogOpen(false);
        setEditingVariant(null);
        form.reset();
    }

    async function onSubmit(values: z.infer<typeof variantFormSchema>) {
        setIsSubmitting(true);
        const url = editingVariant ? `/api/admin/variants/${editingVariant.id}` : '/api/admin/variants';
        const method = editingVariant ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values),
            });

            if (!response.ok) throw new Error(`Failed to ${editingVariant ? 'update' : 'create'} variant`);
            
            const resultVariant = await response.json();

            if (editingVariant) {
                setVariants(prev => prev.map(v => (v.id === resultVariant.id ? resultVariant : v)));
                toast({ title: "Success", description: "Variant updated successfully." });
            } else {
                setVariants(prev => [...prev, resultVariant]);
                toast({ title: "Success", description: "Variant added successfully." });
            }
            handleDialogClose();

        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: `Failed to ${editingVariant ? 'update' : 'create'} variant.` });
        } finally {
            setIsSubmitting(false);
        }
    }
    
    async function handleDeleteConfirm() {
        if (!variantToDelete) return;
        setIsSubmitting(true);
        try {
            const response = await fetch(`/api/admin/variants/${variantToDelete.id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Failed to delete variant');
            setVariants(prev => prev.filter(v => v.id !== variantToDelete.id));
            toast({ title: "Success", description: "Variant deleted successfully."});
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to delete variant." });
        } finally {
            setIsSubmitting(false);
            setVariantToDelete(null);
        }
    }

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Manage Variants</CardTitle>
                    <Button size="sm" className="gap-2" onClick={() => handleDialogOpen()}>
                        <PlusCircle className="h-4 w-4" />
                        Add Variant
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
                                    <TableHead>Image</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Master Skin</TableHead>
                                    <TableHead>Price</TableHead>
                                    <TableHead>Color</TableHead>
                                    <TableHead className="text-right">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {variants.map((variant) => (
                                    <TableRow key={variant.id}>
                                        <TableCell>
                                            <Image src={variant.image_urls[0]} alt={variant.name} width={40} height={40} className="rounded-md border" data-ai-hint="phone skin" />
                                        </TableCell>
                                        <TableCell>{variant.name}</TableCell>
                                        <TableCell>{getMasterSkinName(variant.master_skin_id)}</TableCell>
                                        <TableCell>${variant.price.toFixed(2)}</TableCell>
                                        <TableCell>
                                            <div className="flex items-center gap-2">
                                                <div className="w-6 h-6 rounded-full border" style={{ backgroundColor: variant.color_hex }}></div>
                                                <span className="font-mono text-xs">{variant.color_hex}</span>
                                            </div>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <DropdownMenu>
                                                <DropdownMenuTrigger asChild>
                                                    <Button variant="ghost" className="h-8 w-8 p-0">
                                                        <span className="sr-only">Open menu</span>
                                                        <MoreHorizontal className="h-4 w-4" />
                                                    </Button>
                                                </DropdownMenuTrigger>
                                                <DropdownMenuContent align="end">
                                                    <DropdownMenuLabel>Actions</DropdownMenuLabel>
                                                    <DropdownMenuItem onSelect={() => handleDialogOpen(variant)}>
                                                        <Edit className="mr-2 h-4 w-4"/>
                                                        Edit
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="text-destructive" onSelect={() => setVariantToDelete(variant)}>
                                                        <Trash2 className="mr-2 h-4 w-4"/>
                                                        Delete
                                                    </DropdownMenuItem>
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </CardContent>
            </Card>

             <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogContent className="sm:max-w-[425px]" onInteractOutside={handleDialogClose}>
                    <DialogHeader>
                        <DialogTitle>{editingVariant ? 'Edit Variant' : 'Add New Variant'}</DialogTitle>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 pt-4">
                            <FormField
                                control={form.control}
                                name="master_skin_id"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Master Skin</FormLabel>
                                        <Select onValueChange={field.onChange} value={field.value}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select a master skin" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                {masterSkins.map(skin => (
                                                    <SelectItem key={skin.id} value={String(skin.id)}>{skin.name} (for {skin.model_name})</SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="name"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Variant Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g. Cosmic Marble" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                             <div className="grid grid-cols-2 gap-4">
                                <FormField control={form.control} name="price" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Price</FormLabel>
                                        <FormControl><Input type="number" placeholder="24.99" {...field} value={field.value ?? ""} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}/>
                                <FormField control={form.control} name="color_hex" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Color Hex</FormLabel>
                                        <FormControl><Input placeholder="#FFFFFF" {...field} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}/>
                            </div>
                            <FormItem>
                                <FormLabel>Image</FormLabel>
                                <FormControl><Input id="image" type="file" /></FormControl>
                                <FormMessage />
                                <p className="text-xs text-muted-foreground pt-1">Image upload is for demonstration only.</p>
                            </FormItem>

                            <DialogFooter className="pt-4">
                                <Button type="button" variant="ghost" onClick={handleDialogClose}>Cancel</Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {editingVariant ? 'Save Changes' : 'Save Variant'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!variantToDelete} onOpenChange={(open) => !open && setVariantToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>This action cannot be undone. This will permanently delete the skin variant.</AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setVariantToDelete(null)}>Cancel</AlertDialogCancel>
                        <AlertDialogAction onClick={handleDeleteConfirm} disabled={isSubmitting} className="bg-destructive hover:bg-destructive/90">
                            {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                            Continue
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </>
    )
}
