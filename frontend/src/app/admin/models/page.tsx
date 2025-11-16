'use client';

import { useState, useEffect } from "react";
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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { PlusCircle, MoreHorizontal, Loader2, Edit, Trash2 } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import type { PhoneModel, Brand } from "@/lib/types";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";

const modelFormSchema = z.object({
    name: z.string().min(2, "Model name must be at least 2 characters."),
    brand_id: z.string({ required_error: "Please select a brand." }),
});


export default function AdminModelsPage() {
    const [models, setModels] = useState<PhoneModel[]>([]);
    const [brands, setBrands] = useState<Brand[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingModel, setEditingModel] = useState<PhoneModel | null>(null);
    const [modelToDelete, setModelToDelete] = useState<PhoneModel | null>(null);
    const { toast } = useToast();

    const form = useForm<z.infer<typeof modelFormSchema>>({
        resolver: zodResolver(modelFormSchema),
        defaultValues: {
            name: "",
        },
    });

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const [modelsRes, brandsRes] = await Promise.all([
                    fetch('/api/admin/models'),
                    fetch('/api/admin/brands')
                ]);
                const modelsData = await modelsRes.json();
                const brandsData = await brandsRes.json();
                setModels(modelsData);
                setBrands(brandsData);
            } catch (error) {
                console.error("Failed to fetch data", error);
                 toast({ variant: "destructive", title: "Error", description: "Failed to fetch data." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [toast]);
    
    const handleDialogOpen = (model: PhoneModel | null = null) => {
        if (model) {
            setEditingModel(model);
            form.reset({ name: model.name, brand_id: String(model.brand_id) });
        } else {
            setEditingModel(null);
            form.reset({ name: "", brand_id: undefined });
        }
        setIsDialogOpen(true);
    };

    const handleDialogClose = () => {
        setIsDialogOpen(false);
        setEditingModel(null);
        form.reset();
    }

    async function onSubmit(values: z.infer<typeof modelFormSchema>) {
        setIsSubmitting(true);
        const url = editingModel ? `/api/admin/models/${editingModel.id}` : '/api/admin/models';
        const method = editingModel ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values),
            });

            if (!response.ok) {
                throw new Error(`Failed to ${editingModel ? 'update' : 'create'} model`);
            }

            const resultModel = await response.json();
            const resultWithBrand = { ...resultModel, brand_name: getBrandName(resultModel.brand_id) };


            if (editingModel) {
                setModels(prev => prev.map(m => (m.id === resultWithBrand.id ? resultWithBrand : m)));
                toast({ title: "Success", description: "Model updated successfully." });
            } else {
                setModels(prev => [...prev, resultWithBrand]);
                toast({ title: "Success", description: "Model added successfully." });
            }
            handleDialogClose();

        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: `Failed to ${editingModel ? 'update' : 'create'} model.` });
        } finally {
            setIsSubmitting(false);
        }
    }
    
    async function handleDeleteConfirm() {
        if (!modelToDelete) return;
        setIsSubmitting(true);
        try {
            const response = await fetch(`/api/admin/models/${modelToDelete.id}`, { method: 'DELETE' });
            if (!response.ok) {
                throw new Error('Failed to delete model');
            }
            setModels(prev => prev.filter(m => m.id !== modelToDelete.id));
            toast({ title: "Success", description: "Model deleted successfully."});
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to delete model." });
        } finally {
            setIsSubmitting(false);
            setModelToDelete(null);
        }
    }


    const getBrandName = (brandId: number) => {
        return brands.find(b => b.id === brandId)?.name || 'Unknown';
    };

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Manage Phone Models</CardTitle>
                    <Button size="sm" className="gap-2" onClick={() => handleDialogOpen()}>
                        <PlusCircle className="h-4 w-4" />
                        Add Model
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
                                    <TableHead>Brand</TableHead>
                                    <TableHead className="text-right">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {models.map((model) => (
                                    <TableRow key={model.id}>
                                        <TableCell className="font-medium">{model.id}</TableCell>
                                        <TableCell>{model.name}</TableCell>
                                        <TableCell>{getBrandName(model.brand_id)}</TableCell>
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
                                                    <DropdownMenuItem onSelect={() => handleDialogOpen(model)}>
                                                        <Edit className="mr-2 h-4 w-4" />
                                                        Edit
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="text-destructive" onSelect={() => setModelToDelete(model)}>
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
                        <DialogTitle>{editingModel ? 'Edit Model' : 'Add New Model'}</DialogTitle>
                        <DialogDescription>
                             {editingModel ? 'Update the details for this model.' : 'Add a new phone model to a brand.'}
                        </DialogDescription>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 pt-4">
                            <FormField
                                control={form.control}
                                name="brand_id"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Brand</FormLabel>
                                        <Select onValueChange={field.onChange} value={field.value}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select a brand" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                {brands.map(brand => (
                                                    <SelectItem key={brand.id} value={String(brand.id)}>{brand.name}</SelectItem>
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
                                        <FormLabel>Model Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g. iPhone 15 Pro" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <DialogFooter className="pt-4">
                                <Button type="button" variant="ghost" onClick={handleDialogClose}>Cancel</Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {editingModel ? 'Save Changes' : 'Save Model'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!modelToDelete} onOpenChange={(open) => !open && setModelToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This action cannot be undone. This will permanently delete the model
                            and all associated skins.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setModelToDelete(null)}>Cancel</AlertDialogCancel>
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
