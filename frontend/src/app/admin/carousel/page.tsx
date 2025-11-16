'use client';

import { useState, useEffect } from "react";
import Image from "next/image";
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
import { Textarea } from "@/components/ui/textarea"
import { PlusCircle, MoreHorizontal, Loader2, Edit, Trash2 } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import type { CarouselSlide } from "@/lib/types";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useToast } from "@/hooks/use-toast";

const slideFormSchema = z.object({
    headline: z.string().min(5, "Headline must be at least 5 characters."),
    description: z.string().min(10, "Description must be at least 10 characters."),
    imageUrl: z.string().url("Must be a valid URL."),
    imageHint: z.string().max(20, "Hint must be 20 characters or less."),
    ctaText: z.string().min(3, "CTA Text must be at least 3 characters."),
    ctaLink: z.string().min(1).refine(val => val.startsWith('/'), { message: "CTA Link must be a relative path (e.g., /products)"}),
});

export default function AdminCarouselPage() {
    const [slides, setSlides] = useState<CarouselSlide[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingSlide, setEditingSlide] = useState<CarouselSlide | null>(null);
    const [slideToDelete, setSlideToDelete] = useState<CarouselSlide | null>(null);
    const { toast } = useToast();

    const form = useForm<z.infer<typeof slideFormSchema>>({
        resolver: zodResolver(slideFormSchema),
        defaultValues: { headline: "", description: "", imageUrl: "", imageHint: "", ctaText: "", ctaLink: "/" },
    });

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                const response = await fetch('/api/admin/carousel');
                if (!response.ok) throw new Error("Failed to fetch slides");
                const data = await response.json();
                setSlides(data);
            } catch (error) {
                console.error("Failed to fetch slides", error);
                toast({ variant: "destructive", title: "Error", description: "Failed to fetch carousel slides." });
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [toast]);
    
    const handleDialogOpen = (slide: CarouselSlide | null = null) => {
        if (slide) {
            setEditingSlide(slide);
            form.reset(slide);
        } else {
            setEditingSlide(null);
            form.reset({ headline: "", description: "", imageUrl: "", imageHint: "", ctaText: "", ctaLink: "/" });
        }
        setIsDialogOpen(true);
    }
    
    const handleDialogClose = () => {
        setIsDialogOpen(false);
        setEditingSlide(null);
        form.reset();
    }

    async function onSubmit(values: z.infer<typeof slideFormSchema>) {
        setIsSubmitting(true);
        const url = editingSlide ? `/api/admin/carousel/${editingSlide.id}` : '/api/admin/carousel';
        const method = editingSlide ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values),
            });

            if (!response.ok) throw new Error(`Failed to ${editingSlide ? 'update' : 'create'} slide`);
            
            const resultSlide = await response.json();

            if (editingSlide) {
                setSlides(prev => prev.map(s => (s.id === resultSlide.id ? resultSlide : s)));
                toast({ title: "Success", description: "Carousel slide updated successfully." });
            } else {
                setSlides(prev => [...prev, resultSlide]);
                toast({ title: "Success", description: "Carousel slide added successfully." });
            }
            handleDialogClose();

        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: `Failed to ${editingSlide ? 'update' : 'create'} slide.` });
        } finally {
            setIsSubmitting(false);
        }
    }
    
    async function handleDeleteConfirm() {
        if (!slideToDelete) return;
        setIsSubmitting(true);
        try {
            const response = await fetch(`/api/admin/carousel/${slideToDelete.id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('Failed to delete slide');
            
            setSlides(prev => prev.filter(s => s.id !== slideToDelete.id));
            toast({ title: "Success", description: "Slide deleted successfully."});
        } catch (error) {
            console.error(error);
            toast({ variant: "destructive", title: "Error", description: "Failed to delete slide." });
        } finally {
            setIsSubmitting(false);
            setSlideToDelete(null);
        }
    }

    return (
        <>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                    <CardTitle>Manage Hero Carousel</CardTitle>
                    <Button size="sm" className="gap-2" onClick={() => handleDialogOpen()}>
                        <PlusCircle className="h-4 w-4" />
                        Add Slide
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
                                    <TableHead>Headline</TableHead>
                                    <TableHead>CTA</TableHead>
                                    <TableHead className="text-right">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {slides.map((slide) => (
                                    <TableRow key={slide.id}>
                                        <TableCell>
                                            <Image src={slide.imageUrl} alt={slide.headline} width={120} height={50} className="rounded-md border object-cover" data-ai-hint={slide.imageHint} />
                                        </TableCell>
                                        <TableCell className="font-medium">{slide.headline}</TableCell>
                                        <TableCell>{slide.ctaText}</TableCell>
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
                                                    <DropdownMenuItem onSelect={() => handleDialogOpen(slide)}>
                                                        <Edit className="mr-2 h-4 w-4"/> Edit
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem className="text-destructive" onSelect={() => setSlideToDelete(slide)}>
                                                        <Trash2 className="mr-2 h-4 w-4"/> Delete
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
                <DialogContent className="sm:max-w-xl" onInteractOutside={handleDialogClose}>
                    <DialogHeader>
                        <DialogTitle>{editingSlide ? 'Edit Slide' : 'Add New Slide'}</DialogTitle>
                        <DialogDescription>
                            Fill in the details for the hero carousel slide.
                        </DialogDescription>
                    </DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 pt-4">
                            <FormField control={form.control} name="headline" render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Headline</FormLabel>
                                    <FormControl><Input placeholder="Define Your Device" {...field} /></FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}/>
                             <FormField control={form.control} name="description" render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Description</FormLabel>
                                    <FormControl><Textarea placeholder="Discover exclusive, high-quality skins..." {...field} /></FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}/>
                            <FormField control={form.control} name="imageUrl" render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Image URL</FormLabel>
                                    <FormControl><Input placeholder="https://placehold.co/1920x800.png" {...field} /></FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}/>
                             <FormField control={form.control} name="imageHint" render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Image AI Hint</FormLabel>
                                    <FormControl><Input placeholder="e.g. abstract texture" {...field} /></FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}/>
                            <div className="grid grid-cols-2 gap-4">
                               <FormField control={form.control} name="ctaText" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>CTA Button Text</FormLabel>
                                        <FormControl><Input placeholder="Shop Now" {...field} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}/>
                                 <FormField control={form.control} name="ctaLink" render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>CTA Button Link</FormLabel>
                                        <FormControl><Input placeholder="/products" {...field} /></FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}/>
                            </div>
                            <DialogFooter className="pt-4">
                                <Button type="button" variant="ghost" onClick={handleDialogClose}>Cancel</Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                    {editingSlide ? 'Save Changes' : 'Save Slide'}
                                </Button>
                            </DialogFooter>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!slideToDelete} onOpenChange={(open) => !open && setSlideToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            This action cannot be undone. This will permanently delete the carousel slide.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => setSlideToDelete(null)}>Cancel</AlertDialogCancel>
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
