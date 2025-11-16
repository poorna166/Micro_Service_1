'use client';

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { Input } from "@/components/ui/input"
import { PlusCircle, MoreHorizontal, Loader2, Edit, Trash2 } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import type { Brand } from "@/lib/types";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";

const brandFormSchema = z.object({
    name: z.string().min(2, {
        message: "Brand name must be at least 2 characters.",
    }),
});

export default function AdminBrandsPage() {
    const [brands, setBrands] = useState<Brand[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingBrand, setEditingBrand] = useState<Brand | null>(null);
    const [brandToDelete, setBrandToDelete] = useState<Brand | null>(null);

    const { toast } = useToast();

    const form = useForm<z.infer<typeof brandFormSchema>>({
        resolver: zodResolver(brandFormSchema),
        defaultValues: {
            name: "",
        },
    });

    useEffect(() => {
        const fetchBrands = async () => {
            setIsLoading(true);
            try {
                const response = await fetch('/api/admin/brands');
                const data = await response.json();
                setBrands(data);
            } catch (error) {
                console.error("Failed to fetch brands", error);
                toast({ variant: "destructive", title: "Error", description: "Failed to fetch brands." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchBrands();
    }, [toast]);

    const handleDialogOpen = (brand: Brand | null = null) => {
        if (brand) {
            setEditingBrand(brand);
            form.reset({ name: brand.name });
        } else {
            setEditingBrand(null);
            form.reset({ name: "" });
        }
        setIsDialogOpen(true);
    }
    
    const handleDialogClose = () => {
        setIsDialogOpen(false);
        setEditingBrand(null);
        form.reset();
    }

    async function onSubmit(values: z.infer<typeof brandFormSchema>) {
        setIsSubmitting(true);
        const url = editingBrand ? `/api/admin/brands/${editingBrand.id}` : '/api/admin/brands';
        const method = editingBrand ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values),
            });

            if (!response.ok) {
                throw new Error(`Failed to ${editingBrand ? 'update' : 'create'} brand`);
            }

            const resultBrand = await response.json();

            if (editingBrand) {
                setBrands(prev => prev.map(b => (b.id === resultBrand.id ? resultBrand : b)));
                toast({ title: "Success", description: "Brand updated successfully." });
            } else {
                setBrands(prev => [...prev, resultBrand]);
                toast({ title: "Success", description: "Brand added successfully." });
            }
            handleDialogClose();

        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: `Failed to ${editingBrand ? 'update' : 'create'} brand.` });
        } finally {
            setIsSubmitting(false);
        }
    }

    async function handleDeleteConfirm() {
        if (!brandToDelete) return;
        setIsSubmitting(true);
        try {
            const response = await fetch(`/api/admin/brands/${brandToDelete.id}`, { method: 'DELETE' });
            if (!response.ok) {
                throw new Error('Failed to delete brand');
            }
            setBrands(prev => prev.filter(b => b.id !== brandToDelete.id));
            toast({ title: "Success", description: "Brand deleted successfully."});
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to delete brand." });
        } finally {
            setIsSubmitting(false);
            setBrandToDelete(null);
        }
    }


    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Manage Brands</CardTitle>
                    <Button size="sm" className="gap-2" onClick={() => handleDialogOpen()}>
                        <PlusCircle className="h-4 w-4" />
                        Add Brand
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
                                    <TableHead>ID</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead className="text-right">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {brands.map((brand) => (
                                    <TableRow key={brand.id}>
                                        <TableCell className="font-medium">{brand.id}</TableCell>
                                        <TableCell>{brand.name}</TableCell>
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
                                                    <DropdownMenuItem onSelect={() => handleDialogOpen(brand)}>
                                                        <Edit className="mr-2 h-4 w-4" />
                                                        Edit
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="text-destructive" onSelect={() => setBrandToDelete(brand)}>
                                                        <Trash2 className="mr-2 h-4 w-4" />
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
                <DialogContent onInteractOutside={handleDialogClose}>
                    <DialogHeader>
                        <DialogTitle>{editingBrand ? 'Edit Brand' : 'Add New Brand'}</DialogTitle>
                        <DialogDescription>
                            {editingBrand ? 'Update the details for this brand.' : 'Add a new phone brand to your store.'}
                        </DialogDescription>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Brand Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g. Apple" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <DialogFooter>
                                <Button type="button" variant="ghost" onClick={handleDialogClose}>Cancel</Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {editingBrand ? 'Save Changes' : 'Save Brand'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!brandToDelete} onOpenChange={(open) => !open && setBrandToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This action cannot be undone. This will permanently delete the brand
                            and may affect associated models and skins.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setBrandToDelete(null)}>Cancel</AlertDialogCancel>
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
