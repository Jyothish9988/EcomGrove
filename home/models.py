from django.contrib.auth.models import User
from django.db import models
from ckeditor.fields import RichTextField
from django.db.models.signals import pre_save
from django.utils.text import slugify
from django.dispatch import receiver


# Create your models here.

class Slider(models.Model):
    title = models.CharField(max_length=200, null=True, blank=True)
    subtitle = models.CharField(max_length=200, null=True, blank=True)
    image = models.ImageField(upload_to='slider_images/', null=True, blank=True)
    button_text = models.CharField(max_length=50, null=True, blank=True)
    button_link = models.CharField(max_length=200, null=True, blank=True)

    def __str__(self):
        return self.title


class Category(models.Model):
    title = models.CharField(max_length=255)

    def __str__(self) -> str:
        return self.title[:40]


class subCategory(models.Model):
    title = models.CharField(max_length=255, null=True, blank=True)
    category = models.ForeignKey(Category, null=True, on_delete=models.CASCADE)

    def __str__(self):
        return self.title[:40]


class Product(models.Model):
    title = models.CharField(max_length=255, null=True)
    category = models.ForeignKey(Category, null=True, on_delete=models.DO_NOTHING)
    price = models.PositiveBigIntegerField(default=0, null=True)
    discount = models.PositiveIntegerField(default=0, null=True)
    featured_image = models.ImageField(upload_to='Images/product.jpg', null=True, blank=True)
    brand = models.CharField(max_length=255, null=True)
    total = models.PositiveIntegerField(default=0, null=True)
    available = models.PositiveIntegerField(default=0, null=True)
    description = RichTextField(null=True, blank=True)
    product_information = RichTextField(null=True, blank=True)
    tags = models.CharField(max_length=255, null=True, blank=True)
    slug = models.CharField(max_length=555, null=True, blank=True)

    def __str__(self):
        return self.title[:40]


@receiver(pre_save, sender=Product)
def generate_slug(sender, instance, *args, **kwargs):
    if not instance.slug:
        base_slug = slugify(instance.title)
        unique_slug = base_slug

        while Product.objects.filter(slug=unique_slug).exists():
            unique_slug = f"{base_slug}-{instance.id}"
        instance.slug = unique_slug


class Additional_information(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    title = models.CharField(max_length=255, null=True, blank=True)
    spec = models.CharField(max_length=255, null=True, blank=True)


class Additional_image(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    image = models.ImageField(upload_to='Images/Additional_image.jpg', null=True, blank=True)


class TopDealsOfTheDay(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='top_deals_of_the_day')
    start_date = models.DateTimeField()
    end_date = models.DateTimeField()

    def __str__(self):
        return f"Top Deal: {self.product.title}"


class HotTrendingProducts(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='hot_trending_products')
    start_date = models.DateTimeField()
    end_date = models.DateTimeField()

    def __str__(self):
        return f"Trending: {self.product.title}"


class OnSaleProducts(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='on_sale_products')
    discount_percentage = models.DecimalField(max_digits=5, decimal_places=2)

    def __str__(self):
        return f"On Sale: {self.product.title}"


class TopRatedProducts(models.Model):
    product = models.ForeignKey(Product, on_delete=models.CASCADE, related_name='top_rated_products')
    rating = models.DecimalField(max_digits=3, decimal_places=2)

    def __str__(self):
        return f"Top Rated: {self.product.title}"


class Cart(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)


class CartItem(models.Model):
    cart = models.ForeignKey(Cart, related_name='items', on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    quantity = models.PositiveIntegerField(default=1)

    def total_price(self):
        return self.product.price * self.quantity


class Wishlist(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)


class ShippingAddress(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    address = models.CharField(max_length=255, null=True, blank=True)
    city = models.CharField(max_length=255, null=True, blank=True)
    state = models.CharField(max_length=255, null=True, blank=True)
    country = models.CharField(max_length=255, null=True, blank=True)
    postal_code = models.CharField(max_length=255, null=True, blank=True)
    phone_number = models.CharField(max_length=255, null=True, blank=True)

    def __str__(self):
        return self.user.username

