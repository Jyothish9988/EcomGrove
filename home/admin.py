from django.contrib import admin
from .models import *


class CartTabular(admin.TabularInline):
    model = CartItem


class CartAdmin(admin.ModelAdmin):
    inlines = [CartTabular]


class SubcategoryTabular(admin.TabularInline):
    model = subCategory


class CategoryAdmin(admin.ModelAdmin):
    inlines = [SubcategoryTabular]


class AdditionalinfoTabular(admin.TabularInline):
    model = Additional_information


class AdditionalImageTabular(admin.TabularInline):
    model = Additional_image


class ProductAdmin(admin.ModelAdmin):
    inlines = [AdditionalinfoTabular, AdditionalImageTabular]


# Register your models here.
admin.site.register(Slider)
admin.site.register(Category, CategoryAdmin)
admin.site.register(Product, ProductAdmin)
admin.site.register(TopRatedProducts)
admin.site.register(TopDealsOfTheDay)
admin.site.register(HotTrendingProducts)
admin.site.register(OnSaleProducts)
admin.site.register(Cart, CartAdmin)
admin.site.register(ShippingAddress)
admin.site.register(Wishlist)
