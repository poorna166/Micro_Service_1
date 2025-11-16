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
} from "@/components/ui/dialog"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { PlusCircle, MoreHorizontal, Loader2, Edit, Trash2 } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import type { MasterSkin, PhoneModel } from "@/lib/types";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";

const skinFormSchema = z.object({
  name: z.string().min(2, "Skin name must be at least 2 characters."),
  model_id: z.string({ required_error: "Please select a model." }),
});

export default function AdminSkinsPage() {
    const [skins, setSkins] = useState<MasterSkin[]>([]);
    const [models, setModels] = useState<PhoneModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingSkin, setEditingSkin] = useState<MasterSkin | null>(null);
    const [skinToDelete, setSkinToDelete] = useState<MasterSkin | null>(null);
    const { toast } = useToast();

    const form = useForm<z.infer<typeof skinFormSchema>>({
        resolver: zodResolver(skinFormSchema),
        defaultValues: { name: "" },
    });

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const [skinsRes, modelsRes] = await Promise.all([
                    fetch('/api/admin/skins'),
                    fetch('/api/admin/models')
                ]);
                const skinsData = await skinsRes.json();
                const modelsData = await modelsRes.json();
                setSkins(skinsData);
                setModels(modelsData);
            } catch (error) {
                console.error("Failed to fetch data", error);
                toast({ variant: "destructive", title: "Error", description: "Failed to fetch data." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [toast]);
    
    const getModelName = (modelId: number) => {
        return models.find(m => m.id === modelId)?.name || 'Unknown';
    }

    const handleDialogOpen = (skin: MasterSkin | null = null) => {
        if (skin) {
            setEditingSkin(skin);
            form.reset({ name: skin.name, model_id: String(skin.model_id) });
        } else {
            setEditingSkin(null);
            form.reset({ name: "", model_id: undefined });
        }
        setIsDialogOpen(true);
    }
    
    const handleDialogClose = () => {
        setIsDialogOpen(false);
        setEditingSkin(null);
        form.reset();
    }

    async function onSubmit(values: z.infer<typeof skinFormSchema>) {
        setIsSubmitting(true);
        const url = editingSkin ? `/api/admin/skins/${editingSkin.id}` : '/api/admin/skins';
        const method = editingSkin ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values),
            });

            if (!response.ok) throw new Error(`Failed to ${editingSkin ? 'update' : 'create'} master skin`);
            
            const resultSkin = await response.json();
            const resultWithModel = { ...resultSkin, model_name: getModelName(resultSkin.model_id), variants: editingSkin?.variants || [] };

            if (editingSkin) {
                setSkins(prev => prev.map(s => (s.id === resultWithModel.id ? resultWithModel : s)));
                toast({ title: "Success", description: "Master skin updated successfully." });
            } else {
                setSkins(prev => [...prev, resultWithModel]);
                toast({ title: "Success", description: "Master skin added successfully." });
            }
            handleDialogClose();

        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: `Failed to ${editingSkin ? 'update' : 'create'} master skin.` });
        } finally {
            setIsSubmitting(false);
        }
    }
    
    async function handleDeleteConfirm() {
        if (!skinToDelete) return;
        setIsSubmitting(true);
        try {
            const response = await fetch(`/api/admin/skins/${skinToDelete.id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Failed to delete master skin');
            
            setSkins(prev => prev.filter(s => s.id !== skinToDelete.id));
            toast({ title: "Success", description: "Master skin deleted successfully."});
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to delete master skin." });
        } finally {
            setIsSubmitting(false);
            setSkinToDelete(null);
        }
    }

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Manage Master Skins</CardTitle>
                    <Button size="sm" className="gap-2" onClick={() => handleDialogOpen()}>
                        <PlusCircle className="h-4 w-4" />
                        Add Master Skin
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
                                    <TableHead>Model</TableHead>
                                    <TableHead>Variants</TableHead>
                                    <TableHead className="text-right">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {skins.map((skin) => (
                                    <TableRow key={skin.id}>
                                        <TableCell>{skin.id}</TableCell>
                                        <TableCell>{skin.name}</TableCell>
                                        <TableCell>{skin.model_name}</TableCell>
                                        <TableCell>{skin.variants?.length || 0}</TableCell>
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
                                                    <DropdownMenuItem onSelect={() => handleDialogOpen(skin)}>
                                                        <Edit className="mr-2 h-4 w-4"/>
                                                        Edit
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="text-destructive" onSelect={() => setSkinToDelete(skin)}>
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
                        <DialogTitle>{editingSkin ? 'Edit Master Skin' : 'Add New Master Skin'}</DialogTitle>
                        <DialogDescription>
                            {editingSkin ? 'Update the details for this skin.' : 'Add a new master skin to a phone model.'}
                        </DialogDescription>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 pt-4">
                            <FormField
                                control={form.control}
                                name="model_id"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Model</FormLabel>
                                        <Select onValueChange={field.onChange} value={field.value}>
                                            <FormControl>
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select a model" />
                                                </SelectTrigger>
                                            </FormControl>
                                            <SelectContent>
                                                {models.map(model => (
                                                    <SelectItem key={model.id} value={String(model.id)}>{model.name}</SelectItem>
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
                                        <FormLabel>Master Skin Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="e.g. Carbon Fiber" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <DialogFooter className="pt-4">
                                <Button type="button" variant="ghost" onClick={handleDialogClose}>Cancel</Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {editingSkin ? 'Save Changes' : 'Save Skin'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!skinToDelete} onOpenChange={(open) => !open && setSkinToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This action cannot be undone. This will permanently delete the master skin and all its variants.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setSkinToDelete(null)}>Cancel</AlertDialogCancel>
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
