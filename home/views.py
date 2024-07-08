from home.models import *
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth import authenticate, login
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth import login, logout
from django.contrib.auth.decorators import login_required
from django.contrib.auth import logout as auth_logout


@login_required(login_url='/login')
def logout_view(request):
    auth_logout(request)
    return redirect('home')


def login_view(request):
    if request.method == 'POST':
        form = AuthenticationForm(request, request.POST)
        if form.is_valid():
            user = form.get_user()
            login(request, user)
            return redirect('home')  # Replace 'home' with your desired redirect URL
    else:
        form = AuthenticationForm()
    return render(request, 'login.html', {'form': form})


def signup(request):
    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            form.save()
            # Log the user in after registration
            username = form.cleaned_data.get('username')
            raw_password = form.cleaned_data.get('password1')
            user = authenticate(username=username, password=raw_password)
            login(request, user)
            return redirect('home')  # Replace 'home' with your desired redirect URL
    else:
        form = UserCreationForm()
    return render(request, 'signup.html', {'form': form})


# Create your views here.
def home(request):
    categories = Category.objects.all()
    top_deals = TopDealsOfTheDay.objects.all()
    hot_trending_products = HotTrendingProducts.objects.all()
    slides = Slider.objects.all()
    cart = Cart.objects.get(user=request.user)
    cart_items = CartItem.objects.filter(cart=cart)
    for item in cart_items:
        item.subtotal = item.product.price * item.quantity

    total_price = sum(item.subtotal for item in cart_items)

    total_item_count = CartItem.objects.filter(cart=cart).count()

    context = {
        'categories': categories,
        'top_deals': top_deals,
        'hot_trending_products': hot_trending_products,
        'slides': slides,
        'total_price': total_price,
        'total_item_count': total_item_count,
    }
    return render(request, 'home.html', context)


def product_details(request, slug):
    product = Product.objects.get(slug=slug)
    categories = Category.objects.all()
    additional_images = Additional_image.objects.filter(product=product)
    context = {
        'product': product,
        'categories': categories,
        'additional_images': additional_images
    }
    return render(request, 'product_details.html', context)


@login_required(login_url='/login/')
def add_to_cart(request, slug):
    product = get_object_or_404(Product, slug=slug)
    cart, created = Cart.objects.get_or_create(user=request.user)

    quantity = int(request.POST.get('quantity', 1))

    if quantity > product.available:
        quantity = product.available

    cart_item, created = CartItem.objects.get_or_create(cart=cart, product=product)

    if not created:
        cart_item.quantity += quantity
    else:
        cart_item.quantity = quantity

    cart_item.save()

    product.available -= quantity
    product.save()

    return redirect('cart_view')


@login_required(login_url='/login/')
def cart_view(request):
    cart = Cart.objects.get(user=request.user)
    cart_items = CartItem.objects.filter(cart=cart)

    if request.method == 'POST':
        if 'update_cart' in request.POST:
            for item in cart_items:
                quantity = request.POST.get(f'quantity_{item.id}', 1)
                quantity = int(quantity)
                if quantity > 0:
                    item.quantity = quantity
                    item.save()
                    item.product.available += (item.quantity - quantity)
                    item.product.save()
        elif 'remove_item' in request.POST:
            item_id = request.POST.get('remove_item')
            item = CartItem.objects.get(id=item_id)
            item.product.available += item.quantity
            item.product.save()
            item.delete()
        return redirect('cart_view')

    for item in cart_items:
        item.subtotal = item.product.price * item.quantity

    total_price = sum(item.subtotal for item in cart_items)

    context = {
        'cart': cart,
        'cart_items': cart_items,
        'total_price': total_price,
    }
    return render(request, 'cart.html', context)


@login_required
def my_account(request):
    user = request.user
    shipping_address, created = ShippingAddress.objects.get_or_create(user=user)

    if request.method == 'POST':
        username = request.POST.get('username')
        address = request.POST.get('address')
        city = request.POST.get('city')
        state = request.POST.get('state')
        country = request.POST.get('country')
        postal_code = request.POST.get('postal_code')
        phone = request.POST.get('phone')

        # Debugging print statements
        print("Username:", username)
        print("Address:", address)
        print("City:", city)
        print("State:", state)
        print("Country:", country)
        print("Postal Code:", postal_code)
        print("Phone:", phone)

        # Update user profile details
        user.username = username
        user.save()

        # Update or create shipping address
        shipping_address.address = address
        shipping_address.city = city
        shipping_address.state = state
        shipping_address.country = country
        shipping_address.postal_code = postal_code
        shipping_address.phone_number = phone
        shipping_address.save()

        return redirect('my_account')

    context = {
        'shipping_address': shipping_address
    }
    return render(request, 'my_account.html', context)

def products(request):

    return render(request, 'products.html')